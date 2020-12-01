package inrae.p2m2.util

object NameSpace {

  val ns_class_p2m2 = "peak1:"
  val ns_property_p2m2 = "peak2:"
  val ns_category_p2m2 = "peak3:"
  val ns_food = "foodb:"
  val askomics = "asko:"

  val compound = ns_class_p2m2+"Compound"

  case class InternalNamespaceManagement(val property : String,
                                         val transform : (String => String),
                                         val propertyTypeDef : String )

  val ns_all = Map(
    ns_food -> "https://foodb.ca/compounds/",
    ns_class_p2m2       -> "https://metabohub.p2m2.org/ontology/class/",
    ns_property_p2m2    -> "https://metabohub.p2m2.org/ontology/property/",
    ns_category_p2m2    -> "https://metabohub.p2m2.org/ontology/category/",
    "askomics:" -> "http://askomics.org/internal/" ,
    "owl:" -> "http://www.w3.org/2002/07/owl#",
    "rdfs:" -> "http://www.w3.org/2000/01/rdf-schema#",
    "xsd:" -> "http://www.w3.org/2001/XMLSchema#"
  )
  /**
   * Key property => 1. uri property
   *                 2. transform value ( xsd:string, xsd:Double, category )
   *                 3. datatype or objectproperty
   *///
  val p2m2 : Map[String,InternalNamespaceManagement] = Map(
    "iupac" -> InternalNamespaceManagement( (ns_property_p2m2 + "iupac_name"), ( FormatUtil.toXsdString(_) )    ,
         (BuildTurtle.datatypeproperty( (ns_property_p2m2 + "iupac_name") , "iupac" , compound, "xsd:string")) ),

    "inchi" -> InternalNamespaceManagement( (ns_property_p2m2 +"InChI"),
          (FormatUtil.toXsdString(_) ),
      BuildTurtle.datatypeproperty( (ns_property_p2m2 + "InChI") , "InChi" , compound, "xsd:string") ),

    "inchikey" -> InternalNamespaceManagement( (ns_property_p2m2 +"InChIKey"),
          FormatUtil.toXsdString(_) ,
          BuildTurtle.datatypeproperty( (ns_property_p2m2 + "InChIKey") , "InChIKey" , compound, "xsd:string") ),

    "label" -> InternalNamespaceManagement("rdfs:label", FormatUtil.toXsdString(_) , "" ),
    "altlabel" -> InternalNamespaceManagement("rdfs:altlabel" , FormatUtil.toXsdString(_) ,"" ),
    "weight" -> InternalNamespaceManagement( (ns_property_p2m2 +"exact_mass") ,
                   FormatUtil.toXsdDouble(_) ,
                   BuildTurtle.datatypeproperty( (ns_property_p2m2 + "exact_mass") , "Exact Mass" , compound, "xsd:double") ),
    "smiles" -> InternalNamespaceManagement( (ns_property_p2m2 +"can_smiles")  ,
                   FormatUtil.toXsdString(_) ,
                    BuildTurtle.datatypeproperty( (ns_property_p2m2 + "can_smiles") , "Smiles" , compound, "xsd:string") ),

    "cas_number" -> InternalNamespaceManagement( (ns_property_p2m2 +"cas_number") ,
                    FormatUtil.toXsdString(_) ,
                    BuildTurtle.datatypeproperty( (ns_property_p2m2 + "cas_number") , "Cas Number" , compound, "xsd:string") ),

    "state" -> InternalNamespaceManagement( (ns_property_p2m2 +"state") ,
                    FormatUtil.toXsdString(_) ,
                  BuildTurtle.datatypeproperty( (ns_property_p2m2 + "state") , "State" , compound, "xsd:string") ),

    "annot_quality" -> InternalNamespaceManagement( (ns_property_p2m2 +"annotation_quality") ,
         ( (s : String) => BuildTurtle.manage_category("annot_quality",s)) ,
      BuildTurtle.objectproperty( (ns_property_p2m2 + "annotation_quality") ,
                                   "Annotation Quality" ,
                                    compound,ns_category_p2m2+"annotation_qualityCategory") ),

    "class" -> InternalNamespaceManagement( (ns_property_p2m2 +"class")  ,
            (value => BuildTurtle.manage_category("class",value)) ,
      BuildTurtle.objectproperty( (ns_property_p2m2 + "class") ,
        "Class" ,
        compound,ns_category_p2m2+"classCategory") ),

    "subclass" -> InternalNamespaceManagement( (ns_property_p2m2 +"subclass")  ,
             (value => BuildTurtle.manage_category("subclass",value)) ,
      BuildTurtle.objectproperty( (ns_property_p2m2 + "subclass") ,
                "SubClass" ,
                compound,ns_category_p2m2+"subclassCategory") ),

    "superclass" -> InternalNamespaceManagement( (ns_property_p2m2 +"superclass")  ,
             (value => BuildTurtle.manage_category("superclass",value)) ,
      BuildTurtle.objectproperty( (ns_property_p2m2 + "superclass") ,
        "SuperClass" ,
        compound,ns_category_p2m2+"superclassCategory") ),

    "kingdom" -> InternalNamespaceManagement( (ns_property_p2m2 +"kingdom")  ,
             (value => BuildTurtle.manage_category("kingdom",value)) ,
      BuildTurtle.objectproperty( (ns_property_p2m2 + "kingdom") ,
        "Kingdom" ,
        compound,ns_category_p2m2+"kingdomCategory") ),

    "comment" -> InternalNamespaceManagement("rdfs:comment",
             (FormatUtil.toXsdString(_)),""),

    "db_source" -> InternalNamespaceManagement( (ns_property_p2m2 +"source") ,
              FormatUtil.toXsdString(_) ,
            BuildTurtle.datatypeproperty( (ns_property_p2m2 + "source") , "Database" , compound, "xsd:string") ),

    "mass_plush" -> InternalNamespaceManagement( (ns_property_p2m2 +"mass_plush"),
              FormatUtil.toXsdDouble(_)  ,
      BuildTurtle.datatypeproperty( (ns_property_p2m2 + "mass_plush") , "[M+H+]" , compound, "xsd:double") ),

    "mass_minush" -> InternalNamespaceManagement( (ns_property_p2m2 +"mass_minush"),
              FormatUtil.toXsdDouble(_)  ,
      BuildTurtle.datatypeproperty( (ns_property_p2m2 + "mass_minush") , "[M-H+]" , compound, "xsd:double") ),
  )

  val nsfoodb : Map[String,InternalNamespaceManagement] = Map(
    "moldb_iupac" -> p2m2("iupac"),
    "moldb_inchi" -> p2m2("inchi"),
    "moldb_inchikey" -> p2m2("inchikey"),
    "name" -> p2m2("label"),
    "moldb_mono_mass" -> p2m2("weight"),
    "moldb_smiles" -> p2m2("smiles"),
    "cas_number" -> p2m2("cas_number"),
    "state" -> p2m2("state"),
    "annotation_quality" -> p2m2("annot_quality"),
    "klass" ->  p2m2("class"),
    "subklass" ->  p2m2("subclass"),
    "superklass" ->  p2m2("superclass"),
    "kingdom" ->  p2m2("kingdom"),
    "description" ->  p2m2("comment"),
  )

  def foodb( uri : String ) : String = ns_food + uri
}