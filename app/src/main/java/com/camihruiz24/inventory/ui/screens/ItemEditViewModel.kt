package com.camihruiz24.inventory.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camihruiz24.inventory.data.ItemsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel to retrieve and update an item from the [ItemsRepository]'s data source or insert in it.
 */
@HiltViewModel
class ItemEditViewModel @Inject internal constructor(
    savedStateHandle: SavedStateHandle, private val itemsRepository: ItemsRepository
) : ViewModel() {

    /**
     * Holds current item ui state
     */
    var itemUiState by mutableStateOf(ItemUiState())
        private set

    private val itemId: Int = checkNotNull(savedStateHandle[ItemEditDestination.itemIdArg])

    init {
        viewModelScope.launch {
            itemUiState = itemsRepository.getItemStream(itemId) // Flow<Item?>
                .filterNotNull()// Flow<Item>
                .first() // Item
                .toItemUiState(isEntryValid = false) // ItemUiState
        }
    }

    fun updateUiState(itemDetails: ItemDetails) {
        itemUiState =
            ItemUiState(itemDetails = itemDetails, isEntryValid = validateInput(itemDetails))
    }

    /**
     * Los view model no deberían exponer funciones de suspensión pues:
     * 1. le deja la responsabilidad a la ui de controlar el ciclo de vida de la corrutina
     * 2. el testing se dificulta porque para probar la ui se necesita el emulador, lo que lo hace pesado
     * mientras que probar el view model sólo necesita unit testing.
     *
     * ¿Por qué estamos exponiendo aquí esta función de suspensión?
     * rememberCoroutineScope() es una función de componibilidad que muestra un CoroutineScope
     * vinculado a la composición a la que se llama. Puedes usar la función de componibilidad
     * rememberCoroutineScope() cuando desees iniciar una corrutina fuera de un elemento componible
     * y asegurarte de que se cancele después de que el alcance salga de la composición. Puedes
     * usar esta función cuando necesitas controlar el ciclo de vida de las corrutinas de forma
     * manual; por ejemplo, para cancelar una animación cada vez que ocurre un evento de usuario.
     */
    suspend fun saveItem() {
        if (validateInput()) {
            itemsRepository.updateItem(itemUiState.itemDetails.toItem())
        }
    }

    private fun validateInput(uiState: ItemDetails = itemUiState.itemDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && price.isNotBlank() && quantity.isNotBlank()
        }
    }
}