package com.wagdev.inventorymanagement.core.di

import android.app.Application
import androidx.room.Room
import com.wagdev.inventorymanagement.auth_feature.data.repository.LoginRepositoryImpl
import com.wagdev.inventorymanagement.auth_feature.domain.repository.LoginRepository
import com.wagdev.inventorymanagement.clients_feature.data.repository.ClientRepositoryImpl
import com.wagdev.inventorymanagement.clients_feature.domain.repository.ClientRepository
import com.wagdev.inventorymanagement.clients_feature.domain.usecases.AddEditClientUseCase
import com.wagdev.inventorymanagement.clients_feature.domain.usecases.ClientUseCases
import com.wagdev.inventorymanagement.clients_feature.domain.usecases.DeleteClientUseCase
import com.wagdev.inventorymanagement.clients_feature.domain.usecases.GetAllClientsUseCase
import com.wagdev.inventorymanagement.clients_feature.domain.usecases.GetClientByIdUseCase
import com.wagdev.inventorymanagement.core.util.InventoryDatabase
import com.wagdev.inventorymanagement.order_feature.data.repository.OrderDetailRepositoryImpl
import com.wagdev.inventorymanagement.order_feature.data.repository.OrderRepositoryImpl
import com.wagdev.inventorymanagement.order_feature.domain.repository.OrderDetailRepository
import com.wagdev.inventorymanagement.order_feature.domain.repository.OrderRepository
import com.wagdev.inventorymanagement.order_feature.domain.usecases.AddEditOrderUseCase
import com.wagdev.inventorymanagement.order_feature.domain.usecases.DeleteOrderUseCase
import com.wagdev.inventorymanagement.order_feature.domain.usecases.GetAllOrdersUseCase
import com.wagdev.inventorymanagement.order_feature.domain.usecases.GetAllOrdersWithDetailsUseCase
import com.wagdev.inventorymanagement.order_feature.domain.usecases.GetOrderByClientIdUseCase
import com.wagdev.inventorymanagement.order_feature.domain.usecases.GetOrderByIdUseCase
import com.wagdev.inventorymanagement.order_feature.domain.usecases.OrderUseCases
import com.wagdev.inventorymanagement.products_feature.data.repository.ProductRepositoryImpl
import com.wagdev.inventorymanagement.products_feature.domain.repository.ProductRepository
import com.wagdev.inventorymanagement.products_feature.domain.usecases.AddEditProductUseCase
import com.wagdev.inventorymanagement.products_feature.domain.usecases.DeleteProductUseCase
import com.wagdev.inventorymanagement.products_feature.domain.usecases.GetBoxesTotalUseCase
import com.wagdev.inventorymanagement.products_feature.domain.usecases.GetProductByIdUseCase
import com.wagdev.inventorymanagement.products_feature.domain.usecases.GetProductsUseCase
import com.wagdev.inventorymanagement.products_feature.domain.usecases.ProductUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

@Provides
@Singleton
fun provideInventoryDatabase(app:Application):InventoryDatabase{
    return Room.databaseBuilder(
        app,
        InventoryDatabase::class.java,
        InventoryDatabase.DATABASE_NAME
    ).build()
}

    @Provides
    @Singleton
    fun provideProductRepository(db:InventoryDatabase):ProductRepository{
        return ProductRepositoryImpl(db.productDao)

    }

    @Provides
    @Singleton
    fun provideProductUseCase(
        productRepository: ProductRepository
    ):ProductUseCases{
        return ProductUseCases(
            getProductsUseCase = GetProductsUseCase(productRepository),
            getProductByIdUseCase = GetProductByIdUseCase(productRepository),
            deleteProductUseCase = DeleteProductUseCase(productRepository),
            addEditProductUseCase = AddEditProductUseCase(productRepository),
            getBoxesTotal = GetBoxesTotalUseCase(productRepository)
        )
    }
    // provide the repository and UseCases for client

    @Provides
    @Singleton
    fun provideClientRepository(db:InventoryDatabase): ClientRepository {
        return ClientRepositoryImpl(db.clientDao)

    }

    @Provides
    @Singleton
    fun provideClientUseCase(
       clientRepository: ClientRepository
    ):ClientUseCases{
        return ClientUseCases(
            getAllClientsUseCase = GetAllClientsUseCase(clientRepository),
            getClientByIdUseCase = GetClientByIdUseCase(clientRepository),
            deleteClientUseCase = DeleteClientUseCase(clientRepository),
            addEditClientUseCase = AddEditClientUseCase(clientRepository)
        )
    }

    // for order feature we'll provide usecases and repository
    @Provides
    @Singleton
    fun provideOrderRepository(db:InventoryDatabase): OrderRepository {
        return OrderRepositoryImpl(db.orderDao)

    }

    @Provides
    @Singleton
    fun provideOrderUseCase(
orderRepository: OrderRepository
    ): OrderUseCases {
        return OrderUseCases(
          getAllOrdersWithDetailsUseCase = GetAllOrdersWithDetailsUseCase(orderRepository),
            getAllOrdersUseCase = GetAllOrdersUseCase(orderRepository),
            getOrderByIdUseCase = GetOrderByIdUseCase(orderRepository),
            addEditOrderUseCase = AddEditOrderUseCase(orderRepository),
            deleteOrderUseCase = DeleteOrderUseCase(orderRepository),
            getOrderByClientId = GetOrderByClientIdUseCase(orderRepository)
        )
    }
    /// orderDetail providing data

    @Provides
    @Singleton
    fun provideOrderDetailRepository(db:InventoryDatabase): OrderDetailRepository {
        return OrderDetailRepositoryImpl(db.orderDetailDao)

    }

    /// login providing data

    @Provides
    @Singleton
    fun provideLoginRepository(db:InventoryDatabase): LoginRepository {
        return LoginRepositoryImpl(db.loginDao)

    }

}