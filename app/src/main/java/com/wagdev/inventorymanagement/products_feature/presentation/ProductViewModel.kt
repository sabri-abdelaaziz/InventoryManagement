package com.wagdev.inventorymanagement.products_feature.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wagdev.inventorymanagement.products_feature.domain.model.Product
import com.wagdev.inventorymanagement.products_feature.domain.usecases.ProductUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productUseCases: ProductUseCases
):ViewModel(){

    private val _status= MutableStateFlow<ProductStatus>(ProductStatus.Idle)
    val status:StateFlow<ProductStatus> = _status.asStateFlow()

    private val _product= MutableStateFlow<Product?>(null)
    val product:StateFlow<Product?> = _product.asStateFlow()
    init {
        loadProducts()
    }
    fun onEvent(event:ProductEvent, callback: (Product?) -> Unit = {}){
        when(event){
            is ProductEvent.getProducts ->{
                loadProducts()
            }

            is ProductEvent.addEditProduct -> {
                viewModelScope.launch {
                    _status.value = ProductStatus.Loading
                    try {
                        productUseCases.addEditProductUseCase(event.product)
                        loadProducts()
                        _status.value = ProductStatus.Success(emptyFlow()) // Provide actual list after loading
                    } catch (e: Exception) {
                        _status.value = ProductStatus.Error("Error adding product")
                    }
                }

            }

            is ProductEvent.deleteProduct -> {
                viewModelScope.launch {
                    _status.value = ProductStatus.Loading
                    try {
                        productUseCases.deleteProductUseCase(event.product)
                        loadProducts()
                        _status.value = ProductStatus.Success(emptyFlow()) // Provide actual list after loading
                    } catch (e: Exception) {
                        _status.value = ProductStatus.Error("Error deleting product")
                    }
                }
            }

            is ProductEvent.getProductById -> {
                viewModelScope.launch {
                    _product.value=productUseCases.getProductByIdUseCase(event.id)
                    callback(_product.value)
                }

            }
        }
    }
    private fun loadProducts() {
        viewModelScope.launch {
            _status.value = ProductStatus.Loading
            try {
                val products = productUseCases.getProductsUseCase()
                _status.value = ProductStatus.Success(products)
            } catch (e: Exception) {
                _status.value = ProductStatus.Error("Error loading products")
            }
        }
    }



}