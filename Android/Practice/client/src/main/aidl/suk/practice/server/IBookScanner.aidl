// IBookScanner.aidl
package suk.practice.server;
import suk.practice.server.Book;
// Declare any non-default types here with import statements

interface IBookScanner {

    void onScanBook(in List<Book> books);

}
