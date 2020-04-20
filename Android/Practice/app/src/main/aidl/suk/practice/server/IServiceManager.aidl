// IServiceManager.aidl
package suk.practice.server;
import suk.practice.server.Book;
import suk.practice.server.IBookScanner;
// Declare any non-default types here with import statements

interface IServiceManager {

    void addBook(in Book book);

    void registerScanner(IBookScanner scanner);

void getBinder(IBinder binder);
}
