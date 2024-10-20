package com.soda1127.itbookstorecleanarchitecture.response

// https://api.itbook.store/1.0/new
const val NEW_BOOKS_RESPONSE = """
{
  "error": "0",
  "total": "20",
  "books": [
    {
      "title": "An Introduction to C & GUI Programming, 2nd Edition",
      "subtitle": "",
      "isbn13": "9781912047451",
      "price": "${'$'}14.92",
      "image": "https://itbook.store/img/books/9781912047451.png",
      "url": "https://itbook.store/books/9781912047451"
    },
    {
      "title": "Snowflake: The Definitive Guide",
      "subtitle": "Architecting, Designing, and Deploying on the Snowflake Data Cloud",
      "isbn13": "9781098103828",
      "price": "${'$'}58.90",
      "image": "https://itbook.store/img/books/9781098103828.png",
      "url": "https://itbook.store/books/9781098103828"
    },
    {
      "title": "Python for Data Analysis, 3rd Edition",
      "subtitle": "Data Wrangling with pandas, NumPy, and Jupyter",
      "isbn13": "9781098104030",
      "price": "${'$'}36.18",
      "image": "https://itbook.store/img/books/9781098104030.png",
      "url": "https://itbook.store/books/9781098104030"
    },
    {
      "title": "Reliable Machine Learning",
      "subtitle": "Applying SRE Principles to ML in Production",
      "isbn13": "9781098106225",
      "price": "${'$'}43.99",
      "image": "https://itbook.store/img/books/9781098106225.png",
      "url": "https://itbook.store/books/9781098106225"
    },
    {
      "title": "Data Visualization with Python and JavaScript, 2nd Edition",
      "subtitle": "Scrape, Clean, Explore, and Transform Your Data",
      "isbn13": "9781098111878",
      "price": "${'$'}60.99",
      "image": "https://itbook.store/img/books/9781098111878.png",
      "url": "https://itbook.store/books/9781098111878"
    },
    {
      "title": "Learning Microsoft Power BI",
      "subtitle": "Transforming Data into Insights",
      "isbn13": "9781098112844",
      "price": "${'$'}40.46",
      "image": "https://itbook.store/img/books/9781098112844.png",
      "url": "https://itbook.store/books/9781098112844"
    },
    {
      "title": "C++ Software Design",
      "subtitle": "Design Principles and Patterns for High-Quality Software",
      "isbn13": "9781098113162",
      "price": "${'$'}47.03",
      "image": "https://itbook.store/img/books/9781098113162.png",
      "url": "https://itbook.store/books/9781098113162"
    },
    {
      "title": "Terraform: Up and Running, 3rd Edition",
      "subtitle": "Writing Infrastructure as Code",
      "isbn13": "9781098116743",
      "price": "${'$'}41.99",
      "image": "https://itbook.store/img/books/9781098116743.png",
      "url": "https://itbook.store/books/9781098116743"
    },
    {
      "title": "Flutter and Dart Cookbook",
      "subtitle": "Developing Full-Stack Applications for the Cloud",
      "isbn13": "9781098119515",
      "price": "${'$'}50.52",
      "image": "https://itbook.store/img/books/9781098119515.png",
      "url": "https://itbook.store/books/9781098119515"
    },
    {
      "title": "Python Data Science Handbook, 2nd Edition",
      "subtitle": "Essential Tools for Working with Data",
      "isbn13": "9781098121228",
      "price": "${'$'}56.16",
      "image": "https://itbook.store/img/books/9781098121228.png",
      "url": "https://itbook.store/books/9781098121228"
    },
    {
      "title": "Raspberry Pi Cookbook, 4th Edition",
      "subtitle": "Software and Hardware Problems and Solutions",
      "isbn13": "9781098130923",
      "price": "${'$'}14.99",
      "image": "https://itbook.store/img/books/9781098130923.png",
      "url": "https://itbook.store/books/9781098130923"
    },
    {
      "title": "Azure Maps Using Blazor Succinctly",
      "subtitle": "",
      "isbn13": "9781642002263",
      "price": "${'$'}0.00",
      "image": "https://itbook.store/img/books/9781642002263.png",
      "url": "https://itbook.store/books/9781642002263"
    },
    {
      "title": "Full Stack Quarkus and React",
      "subtitle": "Hands-on full stack web development with Java, React, and Kubernetes",
      "isbn13": "9781800562738",
      "price": "${'$'}39.99",
      "image": "https://itbook.store/img/books/9781800562738.png",
      "url": "https://itbook.store/books/9781800562738"
    },
    {
      "title": "Mathematics for Game Programming and Computer Graphics",
      "subtitle": "Explore the essential mathematics for creating, rendering, and manipulating 3D virtual environments",
      "isbn13": "9781801077330",
      "price": "${'$'}49.99",
      "image": "https://itbook.store/img/books/9781801077330.png",
      "url": "https://itbook.store/books/9781801077330"
    },
    {
      "title": "Architecting and Building High-Speed SoCs",
      "subtitle": "Design, develop, and debug complex FPGA-based systems-on-chip",
      "isbn13": "9781801810999",
      "price": "${'$'}32.99",
      "image": "https://itbook.store/img/books/9781801810999.png",
      "url": "https://itbook.store/books/9781801810999"
    },
    {
      "title": "Web Development with Julia and Genie",
      "subtitle": "A hands-on guide to high-performance server-side web development with the Julia programming language",
      "isbn13": "9781801811132",
      "price": "${'$'}39.99",
      "image": "https://itbook.store/img/books/9781801811132.png",
      "url": "https://itbook.store/books/9781801811132"
    },
    {
      "title": "Java Memory Management",
      "subtitle": "A comprehensive guide to garbage collection and JVM tuning",
      "isbn13": "9781801812856",
      "price": "${'$'}34.99",
      "image": "https://itbook.store/img/books/9781801812856.png",
      "url": "https://itbook.store/books/9781801812856"
    },
    {
      "title": "Test-Driven Development with C++",
      "subtitle": "A simple guide to writing bug-free Agile code",
      "isbn13": "9781803242002",
      "price": "${'$'}44.99",
      "image": "https://itbook.store/img/books/9781803242002.png",
      "url": "https://itbook.store/books/9781803242002"
    },
    {
      "title": "Software Test Design",
      "subtitle": "Write comprehensive test plans to uncover critical bugs in web, desktop, and mobile apps",
      "isbn13": "9781804612569",
      "price": "${'$'}44.99",
      "image": "https://itbook.store/img/books/9781804612569.png",
      "url": "https://itbook.store/books/9781804612569"
    },
    {
      "title": "Microservices with Go",
      "subtitle": "Building scalable and reliable microservices with Go",
      "isbn13": "9781804617007",
      "price": "${'$'}29.99",
      "image": "https://itbook.store/img/books/9781804617007.png",
      "url": "https://itbook.store/books/9781804617007"
    }
  ]
}
"""

// https://api.itbook.store/1.0/search/android
const val SEARCH_RESULT_ANDROID =
"""
{
  "error": "0",
  "total": "333",
  "page": "1",
  "books": [
    {
      "title": "Learning Android Game Programming",
      "subtitle": "A Hands-On Guide to Building Your First Android Game",
      "isbn13": "9780321769626",
      "price": "${'$'}8.40",
      "image": "https://itbook.store/img/books/9780321769626.png",
      "url": "https://itbook.store/books/9780321769626"
    },
    {
      "title": "Learning Android Application Programming for the Kindle Fire",
      "subtitle": "A Hands-On Guide to Building Your First Android Application",
      "isbn13": "9780321833976",
      "price": "${'$'}11.96",
      "image": "https://itbook.store/img/books/9780321833976.png",
      "url": "https://itbook.store/books/9780321833976"
    },
    {
      "title": "Introduction to Android Application Development, 4th Edition",
      "subtitle": "Android Essentials",
      "isbn13": "9780321940261",
      "price": "${'$'}4.58",
      "image": "https://itbook.store/img/books/9780321940261.png",
      "url": "https://itbook.store/books/9780321940261"
    },
    {
      "title": "Learn Android Studio",
      "subtitle": "Build Android Apps Quickly and Effectively",
      "isbn13": "9781430266013",
      "price": "${'$'}24.95",
      "image": "https://itbook.store/img/books/9781430266013.png",
      "url": "https://itbook.store/books/9781430266013"
    },
    {
      "title": "Learn Android Studio 3 with Kotlin",
      "subtitle": "Efficient Android App Development",
      "isbn13": "9781484239063",
      "price": "${'$'}26.99",
      "image": "https://itbook.store/img/books/9781484239063.png",
      "url": "https://itbook.store/books/9781484239063"
    },
    {
      "title": "Android Wireless Application Development, 3rd Edition",
      "subtitle": "Volume II: Advanced Topics",
      "isbn13": "9780321813848",
      "price": "${'$'}3.74",
      "image": "https://itbook.store/img/books/9780321813848.png",
      "url": "https://itbook.store/books/9780321813848"
    },
    {
      "title": "The Android Developer's Cookbook, 2nd Edition",
      "subtitle": "Building Applications with the Android SDK",
      "isbn13": "9780321897534",
      "price": "${'$'}33.21",
      "image": "https://itbook.store/img/books/9780321897534.png",
      "url": "https://itbook.store/books/9780321897534"
    },
    {
      "title": "Sams Teach Yourself Android Application Development in 24 Hours, 2nd Edition",
      "subtitle": "",
      "isbn13": "9780672335693",
      "price": "${'$'}4.37",
      "image": "https://itbook.store/img/books/9780672335693.png",
      "url": "https://itbook.store/books/9780672335693"
    },
    {
      "title": "Beginning Android Tablet Application Development",
      "subtitle": "",
      "isbn13": "9781118106730",
      "price": "${'$'}8.99",
      "image": "https://itbook.store/img/books/9781118106730.png",
      "url": "https://itbook.store/books/9781118106730"
    },
    {
      "title": "Enterprise Android",
      "subtitle": "Programming Android Database Applications for the Enterprise",
      "isbn13": "9781118183496",
      "price": "${'$'}4.39",
      "image": "https://itbook.store/img/books/9781118183496.png",
      "url": "https://itbook.store/books/9781118183496"
    }
  ]
}        
"""
