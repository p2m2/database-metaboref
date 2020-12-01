package inrae.p2m2.app.fooddb

import java.io.{BufferedWriter, File, FileWriter}
import scala.util.parsing.json._

import inrae.p2m2.util._

// source file Hello.scala
object ParseFooDb {
  val ns_foodb = "foodb:"
  val h_weigth = 1.007276


  val nsfoodb : Map[String,NameSpace.InternalNamespaceManagement] = Map(
    "moldb_iupac" -> NameSpace.p2m2("iupac"),
    "moldb_inchi" -> NameSpace.p2m2("inchi"),
    "moldb_inchikey" -> NameSpace.p2m2("inchikey"),
    "name" -> NameSpace.p2m2("label"),
    "moldb_mono_mass" -> NameSpace.p2m2("weight"),
    "moldb_smiles" -> NameSpace.p2m2("smiles"),
    "cas_number" -> NameSpace.p2m2("cas_number"),
    "state" -> NameSpace.p2m2("state"),
    "annotation_quality" -> NameSpace.p2m2("annot_quality"),
    "klass" ->  NameSpace.p2m2("class"),
    "subklass" ->  NameSpace.p2m2("subclass"),
    "superklass" ->  NameSpace.p2m2("superclass"),
    "kingdom" ->  NameSpace.p2m2("kingdom"),
    "description" ->  NameSpace.p2m2("comment"),
  )

  /**
   * Main treatment
   * @param args
   */

  def main(args: Array[String]): Unit = {
    println("Foodb parse : " + args.length)

    if (args.length != 2 ) {
      println("1) FoodDb Compound File ?")
      println("2) Output turtle file ?")
      System.exit(1)
    }

    /* reading json file */
    val filename = args(0)

    /* output file */
    val output = args(1)

    println("filename:" + filename)
    val json_content = scala.io.Source.fromFile(filename).mkString

    /* write turlte file */
    val file = new File(output)
    val bw = new BufferedWriter(new FileWriter(file))

    TtlFileWriter.write_header(bw)

    json_content.split("\n").map(x => {
      JSON.parseFull(x) match {
        case Some(e) => {
          val obj = e.asInstanceOf[Map[String,Any]]

          val obj2 : Map[String,String]= obj.map( { case (key,value)  => {
            try {
              val s = nsfoodb(key)
              s.property -> s.transform(value.asInstanceOf[String])
            } catch {
              case _ : Throwable => "null" -> "null"
            }
          }
          }).collect {
            case (key, value) => key.toString -> value.toString
          }.toMap + {
            obj.get("moldb_mono_mass") match {
              case Some(v) => try {
                val s = NameSpace.p2m2("mass_plush")
                val mass = (v.asInstanceOf[String].toDouble+h_weigth).toString
                s.property -> s.transform(mass)
              } catch {
                case _ : Throwable  =>  {
                  //println("Error cast to double:"+v.asInstanceOf[String])
                  "null" -> "null"
                }
              }
              case _ => "null" -> "null"
            }
          } + {
            obj.get("moldb_mono_mass") match {
              case Some(v) =>  try {
                val s = NameSpace.p2m2("mass_minush")
                val mass = (v.asInstanceOf[String].toDouble-h_weigth).toString
                s.property -> s.transform(mass)
              }  catch {
                case _ : Throwable => {
                  //println("Error cast to double:"+v.asInstanceOf[String])
                  "null" -> "null"
                }
              }
              case _ => "null" -> "null"
            }
          } + {
            obj.get("public_id") match {
              case Some(v) =>
                "uri" -> (NameSpace.foodb(v.asInstanceOf[String]))
              case _ => "null" -> "null"
            }
          }

          TtlFileWriter.write_turtle(bw,obj2)
        }
        case None => println("Failed.")
      }
    })

    TtlFileWriter.write_categories(bw)
    TtlAskomicsFileWriter.write_askomics(bw)
    bw.close()
  }

}
