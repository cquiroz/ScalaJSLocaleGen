package locales

import java.text.{DecimalFormat, DecimalFormatSymbols}

import scala.math.max

case class PatternParts(prefix: String, pattern: String, suffix: String)

object PatternParts {
  def apply(pattern: String): PatternParts = PatternParts("", pattern, "")
}

case class DecimalPatterns(positive: PatternParts, negative: Option[PatternParts])

object DecimalFormatUtil {
  // These are the "standard" pattern characters we convert any localized patterns to
  val PatternCharZeroDigit = '0'
  val PatternCharGroupingSeparator = ','
  val PatternCharDecimalSeparator = '.'
  val PatternCharPerMile = '\u2030'
  val PatternCharPercent = '%'
  val PatternCharDigit = '#'
  val PatternCharSeparator = ';'
  val PatternCharExponent = 'E'
  val PatternCharMinus = '-'

  private val allPatternChars = List(PatternCharZeroDigit, PatternCharGroupingSeparator, PatternCharDecimalSeparator,
    PatternCharPerMile, PatternCharPercent, PatternCharDigit, PatternCharSeparator, PatternCharExponent, PatternCharMinus)

  private val numberPatternChars = List(PatternCharZeroDigit, PatternCharDigit, PatternCharDecimalSeparator, PatternCharMinus,
    PatternCharGroupingSeparator, PatternCharExponent)

  def lastIndexOfPattern(pattern: String, symbol: Char): Int = {
    val max = pattern.length - 1
    allPatternChars.filterNot(_ == symbol).map(pattern.lastIndexOf(_, max)).max
  }

  def suffixFor(format: DecimalFormat, symbol: Char): String = {
    val pattern = format.toPattern
    pattern.substring(lastIndexOfPattern(pattern, symbol) + 1, pattern.length).replaceAll(symbol.toString, localizedSymbol(format, symbol).toString)
  }

  def decimalPatternSplit(pattern: String): PatternParts = {
    // Traverse the string to find the suffix and prefix
    val prefix = new StringBuilder()
    val body = new StringBuilder()
    val suffix = new StringBuilder()
    val quoteChar = '''

    // Fast parsing with mutable vars
    var prefixReady = false
    var bodyReady = false
    var skipCount = 0
    for {
      (c, i) <- pattern.zipWithIndex.toList
    } {
      val inPatternChar = numberPatternChars.contains(c)
      if (skipCount == 0) {
        if (c == quoteChar) {
          val nextIdx = pattern.indexOf(quoteChar, i + 1)
          if (nextIdx >= 0) {
            val next = if (nextIdx == i + 1) {
              "'"
            } else {
              pattern.substring(i + 1, nextIdx)
            }
            if (!prefixReady) {
              prefix ++= next
            } else if (!bodyReady) {
              bodyReady = true
              suffix ++= next
            } else {
              suffix ++= next
            }
            skipCount = nextIdx - i
          } else {
            throw new RuntimeException()
          }
        } else {
          if (!prefixReady && !inPatternChar) {
            prefix += c
          } else if (!prefixReady && inPatternChar) {
            prefixReady = true
          }
          if (prefixReady && inPatternChar) {
            body += c
          } else if (prefixReady && !inPatternChar) {
            bodyReady = true
          }
          if (bodyReady && !inPatternChar) {
            suffix += c
          }
        }
      } else {
        skipCount = skipCount - 1
      }
    }

    PatternParts(prefix.toString(), body.toString, suffix.toString)
  }

  def localizeString(str: String, symbols: DecimalFormatSymbols): String = {
    str.map {
      case PatternCharPercent   => symbols.getPercent
      case PatternCharMinus     => symbols.getMinusSign
      case PatternCharPerMile   => symbols.getPerMill
      case PatternCharZeroDigit => symbols.getZeroDigit
      case c                    => c
    }
  }

  def toDecimalPatterns(pattern: String): DecimalPatterns = pattern.split(';').toList match {
    case Nil         => DecimalPatterns(PatternParts(""), None)
    case p :: Nil    => DecimalPatterns(decimalPatternSplit(p), None)
    case p :: n :: _ => DecimalPatterns(decimalPatternSplit(p), Some(decimalPatternSplit(n)))
  }

  def groupingCount(pattern: String): Int = {
    val pat = pattern.filterNot(c => c == PatternCharPercent || !allPatternChars.contains(c))
    val decIndex = pat.lastIndexOf(PatternCharDecimalSeparator)
    val actualDecIndex = if (decIndex < 0) pat.length - 1 else decIndex - 1
    val lastSeparatorIndex = pat.lastIndexOf(PatternCharGroupingSeparator, actualDecIndex)
    if (pat.isEmpty) 3
    else if (lastSeparatorIndex < 0) 0
    else (if (decIndex < 0 ) pat.length - 1 - lastSeparatorIndex else actualDecIndex - lastSeparatorIndex)
  }

  def localizedSymbol(f: DecimalFormat, symbol: Char): Char = {
    symbol match {
      case PatternCharPercent => f.getDecimalFormatSymbols.getPercent
      case PatternCharMinus   => f.getDecimalFormatSymbols.getMinusSign
      case x => x
    }
  }

  implicit class RichString(val s: String) extends AnyVal {
    def toBlankOption: Option[String] = {
      if (s == null) return None

      s.find{!Character.isWhitespace(_)}.map{ _ => s }
    }
  }

  // This probably isn't perfect if there is a malformed pattern ("#0#0.0#0E#0") but should be good enough
  // TODO: Maybe just add a regexp to validate the correctness of the pattern somewhere else
  private def countMinimum(pattern: String, c: Char, trailingCount: Boolean = true): Option[Int] = {
    pattern.indexOf(c) match {
      case -1  => None
      case idx =>
        // If trailing then take everything after the ".", if preceding take until the "." and reverse it
        // to count the zeroes directly before the "."
        val haystack: String = if (trailingCount) pattern.substring(idx+1) else pattern.substring(0, idx).reverse
        Some(haystack.filterNot{_ == PatternCharGroupingSeparator}.takeWhile(_ == PatternCharZeroDigit).size)
    }
  }

  // Expects a non-localized pattern
  def toParsedPattern(pattern: String): ParsedPattern = {
    val patterns = toDecimalPatterns(pattern)

    val prefixAndSuffix: String = patterns.positive.suffix + patterns.positive.suffix

    // These present in the prefix or suffix modify the multiplier
    val hasPercent: Boolean = prefixAndSuffix.exists(_ == PatternCharPercent)
    val hasMile: Boolean = prefixAndSuffix.exists(_ == PatternCharPerMile)
    assert(
      (hasPercent && !hasMile) || (!hasPercent && hasMile) || (!hasPercent && !hasMile),
      "Can either be percent or mile, not both"
    )

    ParsedPattern(
      positivePrefix           = patterns.positive.prefix.toBlankOption,
      positiveSuffix           = patterns.positive.suffix.toBlankOption,
      negativePrefix           = patterns.negative.flatMap{ _.prefix.toBlankOption },
      negativeSuffix           = patterns.negative.flatMap{ _.suffix.toBlankOption },
      defaultNegativePrefix    = if (patterns.negative.isEmpty) patterns.positive.prefix.toBlankOption else None,
      defaultNegativeSuffix    = if (patterns.negative.isEmpty) patterns.positive.suffix.toBlankOption else None,
      multiplier               = if (hasPercent) 100 else if (hasMile) 1000 else 1,
      groupingSize             = groupingCount(patterns.positive.pattern),
      minimumIntegerDigits     = countMinimum(pattern, PatternCharDecimalSeparator, false),
      minimumFractionDigits    = countMinimum(pattern, PatternCharDecimalSeparator, true),
      minimumExponentDigits    = countMinimum(pattern, PatternCharExponent, true)
    )
  }
}
