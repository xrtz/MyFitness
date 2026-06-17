package com.example.myfitness.presentation.model

data class FoodPreset(
    val name: String,
    val caloriesPer100g: Int,
    val proteinPer100g: Float,
    val fatsPer100g: Float,
    val carbsPer100g: Float
)

val CommonFoodPresets: List<FoodPreset> = listOf(
    FoodPreset("Гречка варёная", 92, 3.4f, 0.6f, 19.9f),
    FoodPreset("Рис варёный", 116, 2.4f, 0.4f, 25.8f),
    FoodPreset("Овсянка варёная", 88, 3.0f, 1.7f, 15.0f),
    FoodPreset("Куриное филе", 165, 31.0f, 3.6f, 0f),
    FoodPreset("Говядина тушёная", 187, 18.9f, 12.4f, 0f),
    FoodPreset("Яйцо куриное", 155, 12.7f, 11.5f, 0.7f),
    FoodPreset("Творог 5%", 121, 17.2f, 5.0f, 1.8f),
    FoodPreset("Молоко 2.5%", 52, 2.8f, 2.5f, 4.7f),
    FoodPreset("Кефир 1%", 40, 3.3f, 1.0f, 3.9f),
    FoodPreset("Сыр российский", 363, 26.0f, 29.5f, 0f),
    FoodPreset("Хлеб белый", 265, 8.1f, 3.2f, 50.1f),
    FoodPreset("Хлеб ржаной", 259, 6.6f, 1.2f, 51.1f),
    FoodPreset("Картофель варёный", 82, 2.0f, 0.1f, 17.3f),
    FoodPreset("Макароны варёные", 138, 5.3f, 0.7f, 27.5f),
    FoodPreset("Помидор", 20, 1.1f, 0.2f, 3.8f),
    FoodPreset("Огурец", 15, 0.8f, 0.1f, 2.8f),
    FoodPreset("Морковь", 35, 1.3f, 0.1f, 6.9f),
    FoodPreset("Капуста белокочанная", 28, 1.8f, 0.1f, 4.7f),
    FoodPreset("Яблоко", 52, 0.3f, 0.4f, 11.8f),
    FoodPreset("Банан", 96, 1.5f, 0.1f, 22.4f),
    FoodPreset("Апельсин", 43, 0.9f, 0.2f, 8.4f),
    FoodPreset("Греческий йогурт 2%", 73, 6.5f, 2.0f, 6.5f),
    FoodPreset("Лосось", 208, 20.0f, 13.4f, 0f),
    FoodPreset("Тунец в собств. соку", 96, 22.0f, 0.7f, 0f),
    FoodPreset("Арахисовая паста", 588, 25.1f, 50.4f, 20.0f),
    FoodPreset("Оливковое масло", 884, 0f, 99.8f, 0f),
    FoodPreset("Чечевица варёная", 116, 9.0f, 0.4f, 20.1f),
    FoodPreset("Грудка индейки", 157, 29.0f, 4.0f, 0f),
    FoodPreset("Греча сырая", 313, 12.6f, 3.3f, 57.1f),
    FoodPreset("Рис сырой", 344, 6.7f, 0.7f, 77.8f),
)
