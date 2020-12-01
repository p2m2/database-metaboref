package inrae.p2m2.util

object FormatUtil {
  val string_sep="\""

  def toXsdString(value : String) : String =
    string_sep + org.apache.commons.text.StringEscapeUtils.unescapeJava(value) + string_sep

  def toXsdDouble(value : String) : String =
    string_sep + value.asInstanceOf[String] + string_sep + "^^xsd:double"
}