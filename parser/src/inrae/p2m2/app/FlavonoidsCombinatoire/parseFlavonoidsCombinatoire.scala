package inrae.p2m2.app.flavonoids

import java.io.{BufferedWriter, File, FileInputStream, FileWriter, IOException}

import org.apache.tika.exception._
import org.apache.tika.metadata._
import org.apache.tika.parser._
import org.apache.tika.parser.microsoft.ooxml._
import org.apache.tika.sax._


import inrae.p2m2.util._
import collection.JavaConverters._

object FlavonoidsCombinatoire {

  def parseDouble(s: String) : Boolean = try { Some(s.replace(",",".").toDouble); true } catch { case _ : Throwable => false }

  def main(args: Array[String]): Unit = {
    println("Foodb parse : " + args.length)

    if (args.length != 2) {
      println("1) FlavonoidsCombinatoire Excel File ?")
      println("2) Output turtle file ?")
      System.exit(1)
    }
    println(" ----------------  Excel format ------------------")
    println(" Line Cell : ID NAME MOLECULE M-H/z M+H/z")
    println("Excel file:" + args(0))
    println("Output ttl:" + args(1))

    val output = args(1)

    //detecting the file type
    val handler : BodyContentHandler = new BodyContentHandler(-1)
    val metadata : Metadata = new Metadata()
    val inputstream : FileInputStream = new FileInputStream(new File(args(0)))
    val pcontext : ParseContext = new ParseContext()

    //OOXml parser
    val msofficeparser : OOXMLParser = new OOXMLParser()
    msofficeparser.parse(inputstream, handler, metadata,pcontext)

    val content = handler.toString().split("\n")

    /* write turlte file */
    val file = new File(output)
    val bw = new BufferedWriter(new FileWriter(file))

    TtlFileWriter.write_header(bw)

    content.map( _.split("\t").filter( _ != "") )
           .filter( cells => (cells.length == 6) &&
                             cells(0) != "" &&           //id is not empty
                             parseDouble(cells(3)) &&    //float/double
                             parseDouble(cells(4)) &&    //float/double
                             parseDouble(cells(5)))      //float/double
           .foreach ( cells => {
             //print("line:"+cells(0)); //cells.map( println(_) )
             val label = NameSpace.p2m2("label")
             val formula = NameSpace.p2m2("formula")

             val w = NameSpace.p2m2("weight")
             val weight = cells(3).replace(",",".")

             val obj = Map (
                "uri" -> (NameSpace.ns_inst_p2m2 + cells(0)),
               label.property -> label.transform(cells(1)) ,
               formula.property -> formula.transform(cells(2)) ,
               w.property -> w.transform(weight) ,
             ) + {
               try {
                 val wp = NameSpace.p2m2("mass_plush")
                 wp.property -> wp.transform((weight.toDouble+Const.h_weigth).toString)
               } catch {
                 case _ : Throwable => "null" -> "null"
               }
             } + {
               try {
                 val wm = NameSpace.p2m2("mass_minush")
                 wm.property -> wm.transform((weight.toDouble-Const.h_weigth).toString)
               } catch {
                 case _ : Throwable => "null" -> "null"
               }
             } + {
               NameSpace.p2m2("db_source").property -> NameSpace.p2m2("db_source").transform("FlavonoidsCombinatoire")
             } + {
               "a" -> NameSpace.compound
             }

             TtlFileWriter.write_turtle(bw,obj)
           }  )


    //System.out.println("Contents of the document:" + handler.toString())
    System.out.println("Metadata of the document:")
    val metadataNames : Array[String] = metadata.names()

    metadataNames.foreach( name =>  {
      System.out.println(name + ": " + metadata.get(name))
    })

    val metadataRdf = Map(
      "uri" -> ":dataset",
      "a" -> "dcat:Dataset",
      "dct:creator" -> FormatUtil.toXsdString(metadata.get("dc:creator")),
      "dct:title" -> "null",
      "dct:issued" -> "null"
    )

    TtlFileWriter.write_turtle(bw,metadataRdf)

    //println(CatalogueOntology.catalogueRdf("dataset_exemple",metadataNames.map( name => (mapping(name) -> metadata.get(name)) ).toMap) )

    TtlFileWriter.write_categories(bw)
    TtlAskomicsFileWriter.write_askomics(bw)
    bw.close()

    //println(l)
  }
}