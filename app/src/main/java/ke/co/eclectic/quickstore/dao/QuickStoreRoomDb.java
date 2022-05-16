package ke.co.eclectic.quickstore.dao;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import ke.co.eclectic.quickstore.models.Business;
import ke.co.eclectic.quickstore.models.Category;
import ke.co.eclectic.quickstore.models.Country;
import ke.co.eclectic.quickstore.models.Creditor;
import ke.co.eclectic.quickstore.models.Customer;
import ke.co.eclectic.quickstore.models.InventoryStock;
import ke.co.eclectic.quickstore.models.OfferType;
import ke.co.eclectic.quickstore.models.PaymentType;
import ke.co.eclectic.quickstore.models.Product;
import ke.co.eclectic.quickstore.models.PurchaseOrder;
import ke.co.eclectic.quickstore.models.SalesOrder;
import ke.co.eclectic.quickstore.models.Store;
import ke.co.eclectic.quickstore.models.StoreType;
import ke.co.eclectic.quickstore.models.Supplier;
import ke.co.eclectic.quickstore.models.Unit;
import ke.co.eclectic.quickstore.models.User;

/**
 * The  Quick store room db.
 *  Created by Manduku O. David on 21/12/2018.
 */
@Database(entities = {PaymentType.class,Customer.class,SalesOrder.class,OfferType.class,
        InventoryStock.class,PurchaseOrder.class,
        Supplier.class,Product.class,Unit.class,Category.class,User.class,Business.class,
        Store.class,Country.class,StoreType.class, Creditor.class},
        version = 6)
public abstract class QuickStoreRoomDb extends RoomDatabase {
    //defining  entity/model abstract
    public abstract SupplierDao supplierDao();
    public abstract CustomerDao customerDao();
    public abstract ProductDao productDao();
    public abstract UnitDao unitDao();
    public abstract CategoryDao categoryDao();
    public abstract UserDao userDao();
    public abstract StoreDao storeDao();
    public abstract BusinessDao businessDao();
    public abstract CountryDao countryDao();
    public abstract StoreTypeDao storeTypeDao();
    public abstract PurchaseOrderDao purchaseOrderDao();
    public abstract SalesOrderDao salesOrderDao();
    public abstract InventoryStockDao inventoryStockDao();
    public abstract OfferTypeDao offerTypeDao();
    public abstract PaymentTypeDao paymentTypeDao();
    public abstract CreditorDao creditorDao();


    //implementing singleton
    private static volatile QuickStoreRoomDb INSTANCE;
    private static Callback sRoomDatabaseCallback =
            new Callback(){

                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    public static QuickStoreRoomDb getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (QuickStoreRoomDb.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            QuickStoreRoomDb.class, "quickstore")
                            .fallbackToDestructiveMigration()
                           // .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static  void deleteData(){
        new PopulateDbAsync(INSTANCE).execute();
    }



     static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final UserDao mUserDao;
        private final BusinessDao businessDao;
        private final StoreDao storeDao;
        private final CountryDao countryDao;
        private final StoreTypeDao storeTypeDao;
        private final CategoryDao categoryDao;
        private final UnitDao unitDao;
        private final ProductDao productDao;
        private final SupplierDao supplierDao;
        private final PurchaseOrderDao purchaseOrderDao;
        private final SalesOrderDao salesOrderDao;
        private final InventoryStockDao inventoryStockDao;
        private final OfferTypeDao offerTypeDao;
        private final CustomerDao customerDao;
        private final PaymentTypeDao paymentTypeDao;
        private final CreditorDao creditorDao;

        PopulateDbAsync(QuickStoreRoomDb db) {
            mUserDao = db.userDao();
            businessDao = db.businessDao();
            storeDao = db.storeDao();
            countryDao = db.countryDao();
            storeTypeDao = db.storeTypeDao();
            categoryDao = db.categoryDao();
            unitDao = db.unitDao();
            productDao = db.productDao();
            supplierDao = db.supplierDao();
            purchaseOrderDao = db.purchaseOrderDao();
            salesOrderDao = db.salesOrderDao();
            inventoryStockDao = db.inventoryStockDao();
            offerTypeDao = db.offerTypeDao();
            customerDao = db.customerDao();
            paymentTypeDao = db.paymentTypeDao();
            creditorDao = db.creditorDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
           // mUserDao.deleteAll();
//          offerTypeDao.deleteAll();
            inventoryStockDao.deleteAll();
            purchaseOrderDao.deleteAll();
            salesOrderDao.deleteAll();
            businessDao.deleteAll();
            storeDao.deleteAll();
            productDao.deleteAll();
            supplierDao.deleteAll();
            customerDao.deleteAll();
            unitDao.deleteAll();
            categoryDao.deleteAll();
//          storeTypeDao.deleteAll();
            paymentTypeDao.deleteAll();
            creditorDao.deleteAll();
//          countryDao.deleteAll();
            return null;
        }
    }
}
