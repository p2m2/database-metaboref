package inrae.p2m2.util

import java.io.BufferedWriter

object TtlFileWriter {
  /**
   * Write TTL header
   * @param bw
   */
  def write_header( bw : BufferedWriter ) : Unit = {
    NameSpace.ns_all.keys.map( k => {
      NameSpace.ns_all.get(k) match {
        case Some(r) => bw.write ("@prefix "+ k + " <"+ r +"> .\n")
        case _ => None
      }
    })

    bw.write ("\n")
  }

  /**
   * Write Object
   * @param bw
   * @param obj
   */
  def write_turtle( bw : BufferedWriter, obj : Map[String,String]) : Unit = {

    obj.get("uri") match {
      case Some(uri) => {
        bw.write(uri + " a " + NameSpace.compound + " ;\n")
        bw.write( "   " + NameSpace.ns_property_p2m2+"db_source " + FormatUtil.toXsdString("FOODB")  +  "  ;\n")
        bw.write(obj.keys.filter(_ != "uri").map(k => {
          obj.get(k) match {
            case Some(v) => if (v != FormatUtil.toXsdString("null")) {
              "  " + k + " " + v
            } else { "null" }
            case _ => throw new Error("Object with key:" + k + "can not be empty")
          }
        }).filter( _ != "null").mkString(" ; \n") + " .\n\n")
      }
      case _ => System.err.println("uri keys does not exist in : "+obj.toString)
    }
  }

  def write_categories( bw : BufferedWriter ) : Unit = {
    bw.write("\n\n#######################  Categories Labels ######################\n\n")

    BuildTurtle.categories.map(  { case (k,v) => {
      BuildTurtle.categories.getOrElse(k,Map()).map ( { case (k2,v2) => {
        bw.write(v2 + " rdfs:label \"" + k2 + "\" .\n")
      }})
    }})
  }

}