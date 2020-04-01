// IServiceManager.aidl
package suk.practice.server;
import suk.practice.server.Book;
// Declare any non-default types here with import statements

interface IServiceManager {

    void addBook(in Book book);

}
