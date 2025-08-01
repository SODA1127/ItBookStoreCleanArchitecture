package com.soda1127.itbookstorecleanarchitecture.data.entity

/**
{
"error":"0",
"title":"TypeScript Notes for Professionals",
"subtitle":"",
"authors":"Stack Overflow Community",
"publisher":"Self-publishing",
"language":"English",
"isbn10":"1622115724",
"isbn13":"1001622115721",
"pages":"96",
"year":"2018",
"rating":"0",
"desc":"The TypeScript Notes for Professionals book is compiled from Stack Overflow Documentation, the content is written by the beautiful people at Stack Overflow....",
"price":"$0.00",
"image":"https://itbook.store/img/books/1001622115721.png",
"url":"https://itbook.store/books/1001622115721",
"pdf":{
"Free eBook":"https://www.dbooks.org/d/5592544360-1622115253-9bbc1cd0a894d0c9/"
}
}
 */
data class BookInfoEntity(
    val title: String,
    val subtitle: String,
    val authors: String,
    val publisher: String,
    val language: String,
    val isbn10: String,
    val isbn13: String,
    val pages: Int,
    val year: Int,
    val rating: Float,
    val desc: String,
    val price: String,
    val image: String,
    val url: String,
    val pdf: PDFEntity?
) {

    fun toBookEntity() = BookEntity(
        title,
        subtitle,
        isbn13,
        price,
        image,
        url
    )

}
