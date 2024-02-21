package daniel.brian.ecommerceapp.data

// Describes each category we have in the app.
// The class is sealed because we want to have objects of the category class
sealed class Category(val category: String) {

    data object Chair: Category("Chairs")
    data object Cupboard: Category("Cupboards")
    data object Table: Category("Tables")
    data object Accessory: Category("Accessory")
    data object Furniture: Category("Furniture")

}