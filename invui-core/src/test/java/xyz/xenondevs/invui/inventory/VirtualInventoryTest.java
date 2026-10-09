package xyz.xenondevs.invui.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VirtualInventoryTest {
    
    @Test
    void testThrowOnAirInConstructor() {
        ItemStack dirt = item(Material.DIRT);
        ItemStack air = item(Material.AIR);
        
        assertThrows(IllegalArgumentException.class, () -> new VirtualInventory(null, new ItemStack[] {dirt, air}));
        assertDoesNotThrow(() -> new VirtualInventory(null, new ItemStack[] {dirt, null}));
    }
    
    private static ItemStack item(Material type) {
        ItemStack stack = mock(ItemStack.class);
        when(stack.getType()).thenReturn(type);
        return stack;
    }
    
}
