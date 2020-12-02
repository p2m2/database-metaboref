package inrae.p2m2.util

/**
 * https://www.w3.org/TR/vocab-dcat-2/
 */
object DcatDataset {

}

object DcatCatalogue {

  val keys : Map[String,( String => String )] = Map(
    "dct:creator" -> FormatUtil.toXsdString,
    "dct:title" -> FormatUtil.toXsdString,
    "dct:issued" -> FormatUtil.toXsdString,

  )

  def staticDefinition() : String = {
      """
      |dcat:keyword "metabolomic" ;
      |dct:publisher <https://www6.inrae.fr/p2m2>
      |
      |""".stripMargin
  }

  def catalogueRdf( datasetname : String, values : Map[String,String] ) : String = {

    ":" + datasetname + "a dcat:Dataset ;\n" + values.keys.map (
      k => {
        if (keys.contains(k))
          k + " " + keys(k)( values(k) )
        else
          ""
      }
    ).filter( _ != "").mkString(" ;\n") + staticDefinition() + " .\n"

  }

}