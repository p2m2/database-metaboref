package inrae.p2m2.util

object NameSpace {

  val ns_inst_p2m2 = "peak0:"
  val ns_class_p2m2 = "peak1:"
  val ns_property_p2m2 = "peak2:"
  val ns_category_p2m2 = "peak3:"
  val ns_food = "foodb:"
  val askomics = "asko:"

  val compound = ns_class_p2m2+"Compound"

  val ns_all = Map(
    ns_food -> "https://foodb.ca/compounds/",
    ns_inst_p2m2       -> "https://metabohub.p2m2.org/ontology/instance/",
    ns_class_p2m2       -> "https://metabohub.p2m2.org/ontology/class/",
    ns_property_p2m2    -> "https://metabohub.p2m2.org/ontology/property/",
    ns_category_p2m2    -> "https://metabohub.p2m2.org/ontology/category/",
    "askomics:" -> "http://askomics.org/internal/" ,
    "owl:" -> "http://www.w3.org/2002/07/owl#",
    "rdfs:" -> "http://www.w3.org/2000/01/rdf-schema#",
    "xsd:" -> "http://www.w3.org/2001/XMLSchema#",
    "dct:" -> "http://purl.org/dc/terms/",
    "dcat:" -> "http://www.w3.org/ns/dcat#",
    "dctype:" -> "http://purl.org/dc/dcmitype/",
    "foaf:" -> "http://xmlns.com/foaf/0.1/",
    "skos:" -> "http://www.w3.org/2004/02/skos/core#",
    "vcard:" -> "http://www.w3.org/2006/vcard/ns#",
    ":" -> "https://metabohub.p2m2.org/"
  )

  /**
   * Key property => 1. uri property
   *                 2. transform value ( xsd:string, xsd:double, category )
   *                 3. datatype or objectproperty
   *///
  val p2m2 : Map[String,NameSpaceManagement] = Map(
    "iupac" -> NameSpaceManagement( (ns_property_p2m2 + "iupac_name"), ( FormatUtil.toXsdString(_) )    ,
         (BuildTurtle.datatypeproperty( (ns_property_p2m2 + "iupac_name") , "iupac" , compound, "xsd:string")) ),

    "inchi" -> NameSpaceManagement( (ns_property_p2m2 +"InChI"),
          (FormatUtil.toXsdString(_) ),
      BuildTurtle.datatypeproperty( (ns_property_p2m2 + "InChI") , "InChi" , compound, "xsd:string") ),

    "inchikey" -> NameSpaceManagement( (ns_property_p2m2 +"InChIKey"),
          FormatUtil.toXsdString(_) ,
          BuildTurtle.datatypeproperty( (ns_property_p2m2 + "InChIKey") , "InChIKey" , compound, "xsd:string") ),

    "label" -> NameSpaceManagement("rdfs:label", FormatUtil.toXsdString(_) , "" ),
    "altlabel" -> NameSpaceManagement("rdfs:altlabel" , FormatUtil.toXsdString(_) ,"" ),
    "weight" -> NameSpaceManagement( (ns_property_p2m2 +"exact_mass") ,
                   FormatUtil.toXsdDouble(_) ,
                   BuildTurtle.datatypeproperty( (ns_property_p2m2 + "exact_mass") , "Exact Mass" , compound, "xsd:double") ),
    "smiles" -> NameSpaceManagement( (ns_property_p2m2 +"can_smiles")  ,
                   FormatUtil.toXsdString(_) ,
                    BuildTurtle.datatypeproperty( (ns_property_p2m2 + "can_smiles") , "Smiles" , compound, "xsd:string") ),

    "cas_number" -> NameSpaceManagement( (ns_property_p2m2 +"cas_number") ,
                    FormatUtil.toXsdString(_) ,
                    BuildTurtle.datatypeproperty( (ns_property_p2m2 + "cas_number") , "Cas Number" , compound, "xsd:string") ),

    "state" -> NameSpaceManagement( (ns_property_p2m2 +"state") ,
                    FormatUtil.toXsdString(_) ,
                  BuildTurtle.datatypeproperty( (ns_property_p2m2 + "state") , "State" , compound, "xsd:string") ),

    "annot_quality" -> NameSpaceManagement( (ns_property_p2m2 +"annotation_quality") ,
         ( (s : String) => BuildTurtle.manage_category("annot_quality",s)) ,
      BuildTurtle.objectproperty( (ns_property_p2m2 + "annotation_quality") ,
                                   "Annotation Quality" ,
                                    compound,ns_category_p2m2+"annotation_qualityCategory") ),

    "class" -> NameSpaceManagement( (ns_property_p2m2 +"class")  ,
            (value => BuildTurtle.manage_category("class",value)) ,
      BuildTurtle.objectproperty( (ns_property_p2m2 + "class") ,
        "Class" ,
        compound,ns_category_p2m2+"classCategory") ),

    "subclass" -> NameSpaceManagement( (ns_property_p2m2 +"subclass")  ,
             (value => BuildTurtle.manage_category("subclass",value)) ,
      BuildTurtle.objectproperty( (ns_property_p2m2 + "subclass") ,
                "SubClass" ,
                compound,ns_category_p2m2+"subclassCategory") ),

    "superclass" -> NameSpaceManagement( (ns_property_p2m2 +"superclass")  ,
             (value => BuildTurtle.manage_category("superclass",value)) ,
      BuildTurtle.objectproperty( (ns_property_p2m2 + "superclass") ,
        "SuperClass" ,
        compound,ns_category_p2m2+"superclassCategory") ),

    "kingdom" -> NameSpaceManagement( (ns_property_p2m2 +"kingdom")  ,
             (value => BuildTurtle.manage_category("kingdom",value)) ,
      BuildTurtle.objectproperty( (ns_property_p2m2 + "kingdom") ,
        "Kingdom" ,
        compound,ns_category_p2m2+"kingdomCategory") ),

    "comment" -> NameSpaceManagement("rdfs:comment",
             (FormatUtil.toXsdString(_)),""),

    "db_source" -> NameSpaceManagement( (ns_property_p2m2 +"source") ,
              FormatUtil.toXsdString(_) ,
            BuildTurtle.datatypeproperty( (ns_property_p2m2 + "source") , "Database" , compound, "xsd:string") ),

    "mass_plush" -> NameSpaceManagement( (ns_property_p2m2 +"mass_plush"),
              FormatUtil.toXsdDouble(_)  ,
      BuildTurtle.datatypeproperty( (ns_property_p2m2 + "mass_plush") , "[M+H+]/z" , compound, "xsd:double") ),

    "mass_minush" -> NameSpaceManagement( (ns_property_p2m2 +"mass_minush"),
              FormatUtil.toXsdDouble(_)  ,
      BuildTurtle.datatypeproperty( (ns_property_p2m2 + "mass_minush") , "[M-H+]/z" , compound, "xsd:double") ),

    "formula" -> NameSpaceManagement( (ns_property_p2m2 + "formula"), ( FormatUtil.toXsdString(_) )    ,
      (BuildTurtle.datatypeproperty( (ns_property_p2m2 + "formula") , "formula" , compound, "xsd:string")) ),


  )


  def foodb( uri : String ) : String = ns_food + uri
}