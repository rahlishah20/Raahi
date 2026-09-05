package com.example.data.datasource

import com.example.domain.model.DestinationCategory
import com.example.domain.model.KarachiDestination

object KarachiLocalDataSource {
    val KARACHI_DESTINATIONS = listOf(
        KarachiDestination(
            id = "mazar-e-quaid",
            name = "Mazar-e-Quaid",
            area = "Saddar",
            address = "M.A Jinnah Rd, Karachi",
            latitude = 24.8746,
            longitude = 67.0398,
            category = DestinationCategory.LANDMARK,
            isRecent = true
        ),
        KarachiDestination(
            id = "dolmen-clifton",
            name = "Dolmen Mall Clifton",
            area = "Clifton",
            address = "Marine Drive, Block 4 Clifton, Karachi",
            latitude = 24.8138,
            longitude = 67.0300,
            category = DestinationCategory.SHOPPING,
            isRecent = true
        ),
        KarachiDestination(
            id = "lucky-one",
            name = "Lucky One Mall",
            area = "Gulberg / Federal B Area",
            address = "Main Rashid Minhas Rd, Karachi",
            latitude = 24.9427,
            longitude = 67.0782,
            category = DestinationCategory.SHOPPING,
            isRecent = true
        ),
        KarachiDestination(
            id = "clifton-sea-view",
            name = "Clifton Beach & Sea View",
            area = "Clifton",
            address = "Sea View Road, Clifton, Karachi",
            latitude = 24.7938,
            longitude = 67.0583,
            category = DestinationCategory.LANDMARK,
            isRecent = false
        ),
        KarachiDestination(
            id = "saddar-bazaar",
            name = "Saddar Bazaar & Empress Market",
            area = "Saddar",
            address = "Preedy Street, Saddar, Karachi",
            latitude = 24.8607,
            longitude = 67.0104,
            category = DestinationCategory.LANDMARK,
            isRecent = false
        ),
        KarachiDestination(
            id = "tariq-road",
            name = "Tariq Road",
            area = "PECHS",
            address = "Tariq Rd, Block 2 PECHS, Karachi",
            latitude = 24.8718,
            longitude = 67.0601,
            category = DestinationCategory.SHOPPING,
            isRecent = false
        ),
        KarachiDestination(
            id = "bahadurabad-chowrangi",
            name = "Bahadurabad Chowrangi",
            area = "Bahadurabad",
            address = "Alamgir Road, Bahadurabad, Karachi",
            latitude = 24.8829,
            longitude = 67.0664,
            category = DestinationCategory.SHOPPING,
            isRecent = false
        ),
        KarachiDestination(
            id = "gulshan-iqbal",
            name = "Gulshan-e-Iqbal Block 13",
            area = "Gulshan-e-Iqbal",
            address = "University Road, Gulshan, Karachi",
            latitude = 24.9180,
            longitude = 67.0971,
            category = DestinationCategory.AREA,
            isRecent = false
        ),
        KarachiDestination(
            id = "shahrah-e-faisal",
            name = "Shahrah-e-Faisal Business District",
            area = "Shahrah-e-Faisal",
            address = "Shahrah-e-Faisal, Karachi",
            latitude = 24.8660,
            longitude = 67.0750,
            category = DestinationCategory.WORK,
            isRecent = false
        ),
        KarachiDestination(
            id = "north-nazimabad",
            name = "North Nazimabad Block H",
            area = "North Nazimabad",
            address = "Shershah Suri Rd, North Nazimabad, Karachi",
            latitude = 24.9392,
            longitude = 67.0425,
            category = DestinationCategory.AREA,
            isRecent = false
        ),
        KarachiDestination(
            id = "boat-basin",
            name = "Boat Basin Food Street",
            area = "Clifton",
            address = "Khayaban-e-Roomi, Block 5 Clifton, Karachi",
            latitude = 24.8236,
            longitude = 67.0321,
            category = DestinationCategory.CAFE,
            isRecent = false
        ),
        KarachiDestination(
            id = "dha-phase-6",
            name = "DHA Phase 6",
            area = "DHA",
            address = "Khayaban-e-Shahbaz, DHA Phase 6, Karachi",
            latitude = 24.8021,
            longitude = 67.0654,
            category = DestinationCategory.HOME,
            isRecent = false
        ),
        KarachiDestination(
            id = "karachi-cantt",
            name = "Karachi Cantt Station",
            area = "Karachi Cantt",
            address = "Dr Daud Pota Rd, Karachi Cantt, Karachi",
            latitude = 24.8488,
            longitude = 67.0372,
            category = DestinationCategory.TRANSIT,
            isRecent = false
        )
    )

    val QUICK_SHORTCUTS = listOf(
        KarachiDestination(
            id = "quick-office",
            name = "Office",
            area = "I.I. Chundrigar Rd",
            address = "I.I. Chundrigar Road, City Financial District, Karachi",
            latitude = 24.8510,
            longitude = 67.0050,
            category = DestinationCategory.WORK
        ),
        KarachiDestination(
            id = "quick-home",
            name = "Home",
            area = "DHA Phase 6",
            address = "Khayaban-e-Shahbaz, DHA Phase 6, Karachi",
            latitude = 24.8021,
            longitude = 67.0654,
            category = DestinationCategory.HOME
        ),
        KarachiDestination(
            id = "quick-cafe",
            name = "Clifton Cafe",
            area = "Boat Basin",
            address = "Block 5 Clifton, Boat Basin, Karachi",
            latitude = 24.8236,
            longitude = 67.0321,
            category = DestinationCategory.CAFE
        )
    )
}
