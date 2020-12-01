package inrae.p2m2.util

object BuildTurtle {

  var categories : Map[String, Map[String,String] ] = Map()

  def manage_category(klass: String, value : String ) : String = {

    val valBaseCatUri = NameSpace.ns_category_p2m2 + klass + "_"

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
}