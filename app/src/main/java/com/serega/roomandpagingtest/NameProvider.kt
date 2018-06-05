package com.serega.roomandpagingtest

import java.util.*

/**
 * @author S.A.Bobrischev
 *         Developed by Magora Team (magora-systems.com). 14.05.18.
 */
object NameProvider {
    val random = Random()
    val names = arrayOf(
            "Yolando Petite",
            "Jasmin Aguon",
            "Jackqueline Durbin",
            "Adell Turley",
            "Kelly Dohm",
            "Melvina Howser",
            "Inell Aube",
            "Toby Snowball",
            "Nathalie Shultz",
            "America Rolon",
            "Maynard Levin",
            "Donnette Jimenes",
            "Torie Furness",
            "Janeth Redel",
            "Elina Hardesty",
            "Carissa Mowen",
            "Chelsey Frew",
            "Elois Dilorenzo",
            "Larraine Savoy",
            "Tayna Prom",
            "Elmira Bulkley",
            "Andres Bolenbaugh",
            "Paz Willcutt",
            "Fred Ashburn",
            "Janay Swensen",
            "Olen Rennick",
            "Cole Lesage",
            "Patience Crossno",
            "Cherri Sedgwick",
            "Mathilda Melvin",
            "William Feaster",
            "Mirna Klann",
            "Jim Aston",
            "Pam Hammons",
            "Julee Stockstill",
            "Ilana Flagg",
            "Louetta Siqueiros",
            "Raeann Mcvay",
            "Venetta Bellin",
            "Stacee Carnegie",
            "Almeta Benefiel",
            "Jeni Calvo",
            "Contessa Poynor",
            "Junita Cain",
            "Elbert Mcfate",
            "Windy Buckland",
            "Babara Waxman",
            "Kenia Dugal",
            "Shala Buchler",
            "Milan Bradley"
    )

    fun getName() = names[random.nextInt(names.size-1)]

    fun getAge() = random.nextInt(100)
}