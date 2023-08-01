package com.lrm.kravito.data

import androidx.annotation.DrawableRes
import com.denzcoskun.imageslider.models.SlideModel
import com.lrm.kravito.R

object OffersData {
    val imageList = arrayListOf(
        SlideModel(R.drawable.offer_1),
        SlideModel(R.drawable.offer_2),
        SlideModel(R.drawable.offer_3),
        SlideModel(R.drawable.offer_4),
        SlideModel(R.drawable.offer_5)
    )
}

data class SuggestedFood(
    @DrawableRes
    val foodImage: Int,
    val foodName: String
)

object SuggestedFoodData {
    val suggestedList = listOf(
        SuggestedFood(R.drawable.dosa_icon, "Dosa"),
        SuggestedFood(R.drawable.fried_rice_icon, "Fried Rice"),
        SuggestedFood(R.drawable.biryani_icon, "Biryani"),
        SuggestedFood(R.drawable.sandwich_icon, "Sandwich"),
        SuggestedFood(R.drawable.pizza_icon, "Pizza"),
        SuggestedFood(R.drawable.ice_cream_icon, "Ice Cream"),
        SuggestedFood(R.drawable.fried_chicken_icon, "Fried Chicken"),
        SuggestedFood(R.drawable.roti_icon, "Roti"),
        SuggestedFood(R.drawable.noodles_icon, "Noodles"),
        SuggestedFood(R.drawable.desserts_icon, "Desserts")
    )
}

data class Restaurant(
    val id: Int,
    val name: String,
    @DrawableRes
    val image: Int,
    val rating: String,
    val distance: String,
    val desc: String,
    val location: String,
    val menuId: Int
)

object RestaurantData {
    val restaurantList = listOf(
        Restaurant(
            3001,
            "Maha's Cuisine",
            R.drawable.r9,
            "5.0",
            "1 km",
            "Meals, Biryanis, Noodles, Milk shakes, Desserts",
            "Boyapalem", 501
        ),
        Restaurant(
            3002,
            "Biryani Palace",
            R.drawable.r1,
            "4.0",
            "6 km",
            "All types of Biryanis",
            "Dwarakanagar", 503
        ),
        Restaurant(
            3003,
            "Dosa Point",
            R.drawable.r7,
            "4.3",
            "2 km",
            "Dosa- Masala, Upma, Ravva, Onion, Plain, Egg...",
            "Marikavalsa", 502
        ),
        Restaurant(
            3004,
            "Gatox",
            R.drawable.r11,
            "3.5",
            "7 km",
            "Seethafal flavor is our special",
            "Seethammadhara", 501
        ),
        Restaurant(
            3005,
            "Chikoo's Restaurant",
            R.drawable.r6,
            "4.1",
            "5 km",
            "Tiffins, Meals, Roti, Partha, Noodles",
            "Kommadhi", 501
        ),
        Restaurant(
            3006,
            "Bhavani Multi Cuisine Restaurant",
            R.drawable.r2,
            "2.0",
            "14 km",
            "Starters, Main courses, Snacks",
            "Madhurawada", 501
        ),
        Restaurant(
            3007,
            "Cakes n Cream",
            R.drawable.r3,
            "4.7",
            "3 km",
            "Pastry cakes - all flavors available",
            "Kommadhi", 501
        ),
        Restaurant(
            3008,
            "Meher's Kitchen",
            R.drawable.r8,
            "4.0",
            "1 km",
            "Meals, Biryanis, Noodles, Milk shakes, Desserts",
            "Boyapalem", 501
        ),
        Restaurant(
            3009,
            "Mr Burger",
            R.drawable.r4,
            "4.0",
            "5km",
            "Small, Medium and Large Burgers, Pizza, Sandwiches",
            "Car shed", 501
        ),
        Restaurant(
            3010,
            "Dayaram Sweets",
            R.drawable.r10,
            "4.7",
            "11 km",
            "All varieties of Sweets are available",
            "Maddilapalem", 501
        ),
        Restaurant(
            3011,
            "Swastik Restaurant",
            R.drawable.r12,
            "4.0",
            "1 km",
            "Meals, Biryanis, Noodles, Milk shakes, Desserts",
            "Boyapalem", 501
        )
    )
}

data class Item(
    val name: String,
    val quotedPrice: String,
    val type: Int
)

data class FoodItem(
    val itemCategory: String,
    val itemList: List<Item>
)

data class RestaurantMenu(
    val menuId: Int,
    val categoryList: List<FoodItem>
)

object MenuTypes {
    const val STARTER = "Starter"
    const val MAIN_COURSE = "Main Course"
    const val FRIED_RICE = "Fried Rice"
    const val BEVERAGES = "Beverages"
    const val DESSERTS = "Desserts"
    const val DOSA_SPECIALS = "Dosa Specials"
    const val TIFFINS = "Tiffins"
    const val BIRYANIS = "Biryanis"
}

object RestaurantMenuData {
    val menuList = listOf(
        RestaurantMenu(
            501,
            listOf(
                FoodItem(
                    MenuTypes.STARTER, listOf(
                        Item("Mushroom 65", "279",0),
                        Item("Baby Corn 65", "289", 0),
                        Item("Veg Manchurian", "299", 0),
                        Item("Paneer 65", "309", 0),
                        Item("Chicken 65", "379", 1),
                        Item("Chicken 555", "379",1),
                        Item("Chilly Fried Prawns", "409",1)
                    )
                ),
                FoodItem(
                    MenuTypes.MAIN_COURSE, listOf(
                        Item("Palak Paneer", "309", 0),
                        Item("Kaju Mushroom", "319", 0),
                        Item("Dal Tadka", "249", 0),
                        Item("Paneer Butter Masala", "309",0),
                        Item("Egg Bhurji", "189",1),
                        Item("Chicken Keema Masala", "₹249", 1),
                        Item("Patiyala Chicken", "379", 1)
                    )
                ),
                FoodItem(
                    MenuTypes.FRIED_RICE, listOf(
                        Item("Veg Fried Rice", "210", 0),
                        Item("Paneer Fried Rice", "240", 0),
                        Item("Schezwan Veg Fried Rice", "220", 0),
                        Item("Egg Fried Rice", "240",1),
                        Item("Chicken Fried Rice", "₹270", 1),
                        Item("Prawn Fried Rice", "280", 1)
                    )
                ),
                FoodItem(
                    MenuTypes.BEVERAGES, listOf(
                        Item("Badam Milk", "80", 0),
                        Item("Lassi", "80", 0),
                        Item("Pepsi 120ml", "70", 0)
                    )
                ),
                FoodItem(
                    MenuTypes.DESSERTS, listOf(
                        Item("Gulab Jamun (2 Pcs)", "60", 0),
                        Item("Rasmalai (2 Pcs)", "75", 0),
                        Item("Belgium Chocolate Ice Cream", "100", 0)
                    )
                )
            )
        ),
        RestaurantMenu(
            502,
            listOf(
                FoodItem(
                    MenuTypes.TIFFINS, listOf(
                        Item("Idli", "60",0),
                        Item("Sambar Idli", "85", 0),
                        Item("Curd Vada", "87", 0),
                        Item("Vada", "70", 0),
                        Item("Poori (2 Pcs)", "90", 0),
                        Item("Upma", "65", 0),
                        Item("Mysore Bajji", "95", 0),
                        Item("Parota", "90", 0)
                    )
                ),
                FoodItem(
                    MenuTypes.DOSA_SPECIALS, listOf(
                        Item("Masala Dosa", "90", 0),
                        Item("Onion Rava Dosa", "92", 0),
                        Item("Paneer Masala Dosa", "110", 0),
                        Item("Rava Dosa", "75",0),
                        Item("Plain Pesara", "75",0),
                        Item("Onion Pesara", "90",0),
                        Item("Ghee Masala Dosa", "99",0),
                        Item("Plain Dosa", "75",0),
                        Item("Onion Minapa Dosa", "90",0),
                        Item("Plain Uttappam", "85",0),
                        Item("Onion Uttappam", "96",0),
                        Item("Upma Pesara", "99",0),
                    )
                ),
                FoodItem(
                    MenuTypes.BEVERAGES, listOf(
                        Item("Badam Milk", "50", 0),
                        Item("Lassi", "45", 0),
                        Item("Butter Milk", "30", 0),
                        Item("Pepsi 120ml", "70", 0),
                        Item("Water Bottle", "26", 0)
                    )
                ),
                FoodItem(
                    MenuTypes.DESSERTS, listOf(
                        Item("Gulab Jamun (1 Pc)", "45", 0),
                        Item("Rasmalai (1 Pc)", "50", 0),
                        Item("Belgium Chocolate Ice Cream", "100", 0)
                    )
                )
            )
        ),
        RestaurantMenu(
            503,
            listOf(
                FoodItem(
                    MenuTypes.FRIED_RICE, listOf(
                        Item("Veg Fried Rice", "240",0),
                        Item("Paneer Fried Rice", "300", 0),
                        Item("Mushroom Kaju  Fried Rice", "320", 0),
                        Item("Jeera Rice", "220", 0),
                        Item("Egg Fried Rice", "250", 1),
                        Item("Chicken Fried Rice", "260", 1),
                        Item("Prawn Fried Rice", "300", 1),
                    )
                ),
                FoodItem(
                    MenuTypes.BIRYANIS, listOf(
                        Item("Veg Biryani", "240", 0),
                        Item("Paneer Biryani", "300", 0),
                        Item("Mushroom Biryani", "300", 0),
                        Item("Kaju Paneer Biryani", "320",0),
                        Item("Chicken 65 Biryani", "330",1),
                        Item("Chicken Lollipop Biryani", "360",1),
                        Item("Chicken Tandoori Biryani", "360",1),
                        Item("Chicken Kalmi Biryani", "360",1),
                        Item("Boneless Chicken Biryani", "330",1),
                        Item("Prawns Biryani", "330",1),
                        Item("Chicken Dum Biryani", "280",1),
                        Item("Chicken Mughlai Biryani", "360",1),
                        Item("Fish Biryani", "360",1)
                    )
                ),
                FoodItem(
                    MenuTypes.BEVERAGES, listOf(
                        Item("Badam Milk", "50", 0),
                        Item("Lassi", "45", 0),
                        Item("Butter Milk", "30", 0),
                        Item("Pepsi 120ml", "70", 0),
                        Item("Water Bottle", "26", 0)
                    )
                ),
                FoodItem(
                    MenuTypes.DESSERTS, listOf(
                        Item("Gulab Jamun (1 Pc)", "45", 0),
                        Item("Rasmalai (1 Pc)", "50", 0),
                        Item("Belgium Chocolate Ice Cream", "100", 0)
                    )
                )
            )
        )
    )
}

data class Order(
    val orderItem: Item,
    val orderQuantity: Int
)