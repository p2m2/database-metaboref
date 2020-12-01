package inrae.p2m2.util

import java.io.BufferedWriter

object TtlAskomicsFileWriter {
  def write_askomics(  bw : BufferedWriter ) : Unit = {
    bw.write("\n\n#######################  Askomics Abstraction ######################\n\n")
    bw.write(NameSpace.compound + " a  askomics:entity ,\n")
    bw.write(  "         askomics:startPoint ,\n")
    bw.write(  "         owl:Class ;\n")
    bw.write(  "         rdfs:label \"Compound\" .\n\n")

    NameSpace.p2m2.values.foreach (v => bw.write(v.propertyTypeDef) )


    bw.write("\n\n")

    BuildTurtle.categories.map(  { case (k,v) => {
      bw.write(NameSpace.ns_category_p2m2+k+"Category"  + " askomics:category ")
      bw.write(BuildTurtle.categories.getOrElse(k,Map()).map ( { case (k2,v2) => {v2}}).mkString(","))
      bw.write(" . \n")
    }})

  }
}