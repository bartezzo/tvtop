package pl.tvtop

data class StationGroup(val name: String, val stations: List<String>)

val stationGroups = listOf(
    StationGroup(
        "Ogólnopolskie publiczne",
        listOf(
            "TVP1", "TVP2", "TVP3 (regionalna)", "TVP Info", "TVP Polonia", "TVP Kultura",
            "TVP Historia", "TVP Historia 2", "TVP Seriale", "TVP Rozrywka", "TVP Sport",
            "TVP ABC", "TVP Dokument", "TVP Nauka", "TVP Kobieta", "TVP World", "TVP HD",
            "Belsat TV"
        )
    ),
    StationGroup(
        "Ogólnopolskie komercyjne",
        listOf(
            "Polsat", "TVN", "TV4", "TV Puls", "Puls 2", "TTV", "TVN7", "Polsat 2",
            "Polsat Cafe", "Polsat Film", "Polsat Play", "Polsat News", "Polsat News 2",
            "Polsat Sport", "Polsat Sport Extra", "Polsat Sport Fight", "Polsat Rodzina",
            "Polsat Doku", "Polsat Seriale", "Polsat Games", "Polsat Viasat Explore",
            "Polsat Viasat History", "Polsat Viasat Nature", "Fokus TV", "Super Polsat",
            "TV6", "Stopklatka TV", "WP", "Zoom TV", "Metro", "Nowa TV", "Eska TV",
            "Eska Rock TV", "Polo TV", "Disco Polo Music", "Vox Music TV", "4Fun TV",
            "4Fun Dance", "Active Family", "Kino Polska", "Kino Polska Muzyka",
            "Kino TV", "Filmbox", "Romance TV", "Super Kino", "Kino Polska Seriale"
        )
    ),
    StationGroup(
        "Informacyjne",
        listOf(
            "TVN24", "TVN24 BiS", "TVN Biznes i Świat", "Polsat News", "TV Republika",
            "wPolsce24", "Telewizja Trwam", "TVP Info", "CNBC Europe", "Euronews Polska",
            "Biznes24"
        )
    ),
    StationGroup(
        "Sportowe",
        listOf(
            "Eleven Sports 1", "Eleven Sports 2", "Eleven Sports 3", "Eleven Sports 4",
            "Canal+ Sport", "Canal+ Sport 2", "Canal+ Sport 3", "Canal+ Sport 4",
            "Canal+ Sport 5", "Canal+ Extra", "Eurosport 1", "Eurosport 2",
            "Extreme Sports Channel", "Motowizja", "Golf Channel", "Stadion"
        )
    ),
    StationGroup(
        "Filmowe i serialowe",
        listOf(
            "HBO", "HBO 2", "HBO 3", "Cinemax", "Cinemax 2", "Canal+ Premium",
            "Canal+ Seriale", "Canal+ Film", "Canal+ Family", "Canal+ Dokument",
            "Canal+ 4K Ultra HD", "AXN", "AXN Black", "AXN White", "AXN Spin",
            "Paramount Channel", "Comedy Central", "Epic Drama", "FOX", "FOX Comedy",
            "TNT", "TNT Serie", "Star Channel", "Star Life", "Star Movies", "13 Ulica",
            "Universal Channel", "Sci-Fi", "Ale Kino+", "Scifi", "Dizi Pl"
        )
    ),
    StationGroup(
        "Dokumentalne i naukowe",
        listOf(
            "National Geographic", "Nat Geo Wild", "Nat Geo People", "Discovery Channel",
            "Discovery Science", "Discovery Life", "Investigation Discovery",
            "TLC", "Animal Planet", "History", "History 2", "Crime+Investigation Polsat",
            "BBC Earth", "BBC Brit", "BBC Lifestyle", "BBC First", "Planete+",
            "Planete+ Doc", "Da Vinci", "CBS Reality", "Viasat Explore", "Viasat History",
            "Viasat Nature", "Travel Channel", "Fashion TV"
        )
    ),
    StationGroup(
        "Lifestyle i rozrywka",
        listOf(
            "Food Network", "Cooking Channel", "Kuchnia+", "Domo+", "Polsat Cafe",
            "HGTV", "Lifetime", "Fine Living", "Eska Go", "TVN Style", "TVN Turbo",
            "TVN Fabuła", "Canal+ Kuchnia", "Zdrowie TV", "Wellbeing"
        )
    ),
    StationGroup(
        "Dla dzieci",
        listOf(
            "MiniMini+", "teleTOON+", "Cartoon Network", "Nickelodeon", "Nick Jr.",
            "Nicktoons", "Disney Channel", "Disney Junior", "Disney XD", "Boomerang",
            "Baby TV", "Duck TV", "JimJam", "TVP ABC", "Polsat JimJam", "Da Vinci Kids"
        )
    ),
    StationGroup(
        "Muzyczne",
        listOf(
            "MTV Polska", "MTV Live", "MTV 90s", "VH1 Polska", "Stars.TV", "Mezzo",
            "Slager TV", "Polo TV", "Eska TV", "Eska Rock TV", "Eska Best Music TV",
            "Eska Hot TV", "Eska Summer TV", "Vox Music TV", "4Fun Kids", "Rock Polska",
            "Trace Urban", "Club MTV", "Stingray Classica", "Hip Hop TV"
        )
    ),
    StationGroup(
        "Regionalne i lokalne",
        listOf(
            "TVP3 Białystok", "TVP3 Bydgoszcz", "TVP3 Gdańsk", "TVP3 Gorzów Wielkopolski",
            "TVP3 Katowice", "TVP3 Kielce", "TVP3 Kraków", "TVP3 Lublin", "TVP3 Łódź",
            "TVP3 Olsztyn", "TVP3 Opole", "TVP3 Poznań", "TVP3 Rzeszów", "TVP3 Szczecin",
            "TVP3 Warszawa", "TVP3 Wrocław", "Polsat News (regionalne)", "TV Silesia",
            "Telewizja Kraków", "Regionalna Telewizja Kablowa", "Toya", "TVK Kraków",
            "Telewizja Łódź", "Telewizja Gdynia", "ITV Katowice", "TV Toruń", "TV Poznań"
        )
    )
)
