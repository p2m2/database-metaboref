package inrae.p2m2.app.fooddb

import java.io.{BufferedWriter, File, FileWriter}
import scala.util.parsing.json._

import inrae.p2m2.util._

// source file Hello.scala
object ParseFooDb {
  val ns_foodb = "foodb:"
  val h_weigth = 1.007276


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
              val s = NameSpace.nsfoodb(key)
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
