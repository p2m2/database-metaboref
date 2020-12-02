package inrae.p2m2.util

case class NameSpaceManagement(val property : String,
                               val transform : (String => String),
                               val propertyTypeDef : String )