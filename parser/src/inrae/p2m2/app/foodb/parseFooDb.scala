package inrae.p2m2.app.fooddb

import java.io.{BufferedWriter, File, FileWriter}
import scala.util.parsing.json._

// source file Hello.scala
object ParseFooDb {
  val ns_foodb = "foodb:"
  val ns_class_p2m2 = "peak1:"
  val ns_property_p2m2 = "peak2:"
  val ns_category_p2m2 = "peak3:"
  val askomics = "asko:"

  val ns_all = Map(
    ns_foodb -> "https://foodb.ca/compounds/",
    ns_class_p2m2       -> "https://metabohub.p2m2.org/ontology/class/",
    ns_property_p2m2    -> "https://metabohub.p2m2.org/ontology/property/",
    ns_category_p2m2    -> "https://metabohub.p2m2.org/ontology/category/",
    "askomics:" -> "http://askomics.org/internal/" ,
    "owl:" -> "http://www.w3.org/2002/07/owl#",
    "rdfs:" -> "http://www.w3.org/2000/01/rdf-schema#",
    "xsd:" -> "http://www.w3.org/2001/XMLSchema#"
  )

  val h_weigth = 1.007276

  val string_sep="\"\"\""

  var categories : Map[String, Map[String,String] ] = Map()

  /**
   * Write TTL header
   * @param bw
   */
  def write_header( bw : BufferedWriter ) : Unit = {
    ns_all.keys.map( k => {
      ns_all.get(k) match {
        case Some(r) => bw.write ("@prefix "+ k + " <"+ r +"> .\n")
        case _ => None
      }
    })

    bw.write ("\n")
  }

  def manage_category(klass: String, value : String ) : String = {

    val valBaseCatUri = ns_category_p2m2 + klass + "_"

    categories.get(klass) match {
      case Some(category) => {
        val cat = category.get( value ) match {
          case Some(r) => category
          case None => category +  (value -> (valBaseCatUri + category.toArray.length.toString))
        }
        categories = categories - klass
        categories = categories + ( klass -> cat )
      }
      case None => categories = categories + ( klass -> Map( value -> (valBaseCatUri + "0") ))
    }

    categories.getOrElse(klass,Map("value" -> "undefined")).getOrElse(value,"undefined")
  }

  /**
   * Write Object
   * @param bw
   * @param obj
   */
  def write_turtle( bw : BufferedWriter, obj : Map[String,String]) : Unit = {

    obj.get("uri") match {
      case Some(uri) => {
        bw.write(uri + " a " + ns_class_p2m2 + "Compound ;\n")
        bw.write( "   " + ns_property_p2m2+"db_source " + toXsdString("FOODB")  +  "  ;\n")
        bw.write(obj.keys.filter(_ != "uri").map(k => {
            obj.get(k) match {
              case Some(v) => if (v != (string_sep + "null" + string_sep)) {
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

    categories.map(  { case (k,v) => {
      categories.getOrElse(k,Map()).map ( { case (k2,v2) => {
        bw.write(v2 + " rdfs:label \"" + k2 + "\" .\n")
      }})
    }})
  }

  def datatypeproperty( property:String , label:String,  domain : String , range : String) :String = {
      property + " a " + "owl:DatatypeProperty ;\n"  +
      "         rdfs:label \""+label+"\" ; \n" +
      "         rdfs:domain "+ domain + " ; \n" +
      "         rdfs:range "+range+" . \n\n"
  }

  def objectproperty( property:String , label:String,  domain : String , range : String) :String = {
    property + " a " + "owl:ObjectProperty ;\n"  +
      "         rdfs:label \""+label+"\" ; \n" +
      "         rdfs:domain "+ domain + " ; \n" +
      "         rdfs:range "+range+" . \n\n"
  }

  def objectpropertyAskomicsCategory( property:String , label:String,  domain : String , range : String) :String = {
    property + " a " + "owl:ObjectProperty ;\n"  +
      "         a askomics:AskomicsCategory  ; \n" +
      "         rdfs:label \""+label+"\" ; \n" +
      "         rdfs:domain "+ domain + " ; \n" +
      "         rdfs:range "+range+" . \n\n"
  }

  //a askomics:AskomicsCategory

  def write_askomics(  bw : BufferedWriter ) : Unit = {
    bw.write("\n\n#######################  Askomics Abstraction ######################\n\n")
    bw.write(ns_class_p2m2+"Compound" + " a  askomics:entity ,\n")
    bw.write(  "         askomics:startPoint ,\n")
    bw.write(  "         owl:Class ;\n")
    bw.write(  "         rdfs:label \"Compound\" .\n\n")

    bw.write( datatypeproperty( ns_property_p2m2+"db_source","source",ns_class_p2m2+"Compound","xsd:string"))
    bw.write( datatypeproperty( ns_property_p2m2+"iupac_name","iupac name",ns_class_p2m2+"Compound","xsd:string"))
    bw.write( datatypeproperty( ns_property_p2m2+"InChI","InChI",ns_class_p2m2+"Compound","xsd:string"))
    bw.write( datatypeproperty( ns_property_p2m2+"InChIKey","InChIKey",ns_class_p2m2+"Compound","xsd:string"))
    bw.write( datatypeproperty( ns_property_p2m2+"exact_mass","exact mass",ns_class_p2m2+"Compound","xsd:double"))
    bw.write( datatypeproperty( ns_property_p2m2+"mass_plush","[M+H+]",ns_class_p2m2+"Compound","xsd:double"))
    bw.write( datatypeproperty( ns_property_p2m2+"mass_minush","[M-H+]",ns_class_p2m2+"Compound","xsd:double"))
    bw.write( datatypeproperty( ns_property_p2m2+"can_smiles","smiles",ns_class_p2m2+"Compound","xsd:string"))
    bw.write( datatypeproperty( ns_property_p2m2+"state","state",ns_class_p2m2+"Compound","xsd:string"))
    bw.write( datatypeproperty( ns_property_p2m2+"comment","comment",ns_class_p2m2+"Compound","xsd:string"))

    bw.write( objectpropertyAskomicsCategory( ns_property_p2m2+"annotation_quality","annotation quality",ns_class_p2m2+"Compound",ns_category_p2m2+"annotation_qualityCategory"))
    bw.write( objectpropertyAskomicsCategory( ns_property_p2m2+"class","class",ns_class_p2m2+"Compound",ns_category_p2m2+"klassCategory"))
    bw.write( objectpropertyAskomicsCategory( ns_property_p2m2+"sublass","subclass",ns_class_p2m2+"Compound",ns_category_p2m2+"subKlassCategory"))
    bw.write( objectpropertyAskomicsCategory( ns_property_p2m2+"kingdom","kingdom",ns_class_p2m2+"Compound",ns_category_p2m2+"kingdomCategory"))


    bw.write("\n\n")

    categories.map(  { case (k,v) => {
      bw.write(ns_category_p2m2+k+"Category"  + " askomics:category ")
      bw.write(categories.getOrElse(k,Map()).map ( { case (k2,v2) => {v2}}).mkString(","))
      bw.write(" . \n")
    }})

  }

  def toXsdString(value : String) : String =
    string_sep + org.apache.commons.text.StringEscapeUtils.unescapeJava(value) + string_sep

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

    write_header(bw)

    json_content.split("\n").map(x => {
      JSON.parseFull(x) match {
        case Some(e) => {
          val obj = e.asInstanceOf[Map[String,Any]]

          val obj2 : Map[String,String]= obj.map( { case (key,value)  => {

             key match {
              case "public_id"   => "uri" -> (ns_foodb+value.asInstanceOf[String])
              case "moldb_iupac" => ns_property_p2m2+"iupac_name" -> toXsdString(value.asInstanceOf[String])
              case "moldb_inchi" => ns_property_p2m2+"InChI" -> toXsdString(value.asInstanceOf[String])
              case "moldb_inchikey" => ns_property_p2m2+"InChIKey" -> toXsdString(value.asInstanceOf[String])
              case "name" => "rdfs:label" -> toXsdString(value.asInstanceOf[String])
              case "moldb_mono_mass" => ns_property_p2m2+"exact_mass" -> ("\""+value.asInstanceOf[String]+"\"^^xsd:double")
              case "moldb_smiles" => ns_property_p2m2+"can_smiles" -> toXsdString(value.asInstanceOf[String])
              case "cas_number" => ns_property_p2m2+"cas_number" -> toXsdString(value.asInstanceOf[String])
              case "state" => ns_property_p2m2+"state" -> toXsdString(value.asInstanceOf[String])
              case "annotation_quality" => ns_property_p2m2+"annotation_quality" -> (manage_category("annotation_quality",value.asInstanceOf[String]))
              case "klass" => ns_property_p2m2+"class" -> (manage_category("klass",value.asInstanceOf[String]))
              case "subklass" => ns_property_p2m2+"subclass" -> (manage_category("subklass",value.asInstanceOf[String]))
              case "superklass" => ns_property_p2m2+"superclass" -> (manage_category("superklass",value.asInstanceOf[String]))
              case "kingdom" => ns_property_p2m2+"kingdom" -> (manage_category("kingdom",value.asInstanceOf[String]))
              case "id" => None //no treamemnt -> id database
              case "description" => ns_property_p2m2+"comment" -> toXsdString(value.asInstanceOf[String])
              case _ => println("unkown key :"+key+"  --> "+value ) ; None
            }
          }
          }).collect {
            case (key, value) => key.toString -> value.toString
          }.toMap + {
            obj.get("moldb_mono_mass") match {
              case Some(v) => try {
                ns_property_p2m2+"mass_plush" -> ("\""+(v.asInstanceOf[String].toDouble+h_weigth).toString+"\"^^xsd:double")
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
                ns_property_p2m2+"mass_minush" -> ("\""+(v.asInstanceOf[String].toDouble-h_weigth).toString+"\"^^xsd:double")
              }  catch {
                case _ : Throwable => {
                  //println("Error cast to double:"+v.asInstanceOf[String])
                  "null" -> "null"
                }
              }
              case _ => "null" -> "null"
            }
          }

          write_turtle(bw,obj2)
        }
        case None => println("Failed.")
      }
    })

    write_categories(bw)
    write_askomics(bw)
    bw.close()
  }

}
