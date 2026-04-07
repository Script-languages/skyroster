<script setup>
import { ref } from 'vue'
import { RouterLink, RouterView } from 'vue-router'

const sidebarCollapsed = ref(false)

const menuItems = [
  { label: 'Harmonogram', icon: 'pi pi-calendar', route: '/harmonogram' },
  { label: 'Piloci', icon: 'pi pi-users', route: '/piloci' },
  { label: 'Samoloty', icon: 'pi pi-send', route: '/samoloty' },
  { label: 'Zasady', icon: 'pi pi-book', route: '/zasady' }
]

function toggleSidebar() {
  sidebarCollapsed.value = !sidebarCollapsed.value
}
</script>

<template>
  <div class="app-layout">
    <aside class="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-header">
        <h1 v-if="!sidebarCollapsed" class="logo">SkyRoster</h1>
        <button class="toggle-btn" @click="toggleSidebar">
          <i :class="sidebarCollapsed ? 'pi pi-angle-right' : 'pi pi-angle-left'"></i>
        </button>
      </div>
      <nav class="sidebar-nav">
        <RouterLink
          v-for="item in menuItems"
          :key="item.route"
          :to="item.route"
          class="nav-item"
          active-class="active"
        >
          <i :class="item.icon"></i>
          <span v-if="!sidebarCollapsed" class="nav-label">{{ item.label }}</span>
        </RouterLink>
      </nav>
    </aside>
    <main class="main-content">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.app-layout {
  display: flex;
  min-height: 100vh;
  background-color: #cbd5e1;
}

.sidebar {
  width: 250px;
  background: linear-gradient(180deg, #1e293b 0%, #0f172a 100%);
  color: white;
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
}

.sidebar.collapsed {
  width: 60px;
}

.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo {
  font-size: 1.25rem;
  font-weight: 700;
  margin: 0;
  color: #60a5fa;
}

.toggle-btn {
  background: transparent;
  border: none;
  color: white;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.toggle-btn:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.sidebar-nav {
  display: flex;
  flex-direction: column;
  padding: 1rem 0;
  gap: 0.25rem;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1rem;
  color: #94a3b8;
  text-decoration: none;
  transition: all 0.2s;
  margin: 0 0.5rem;
  border-radius: 6px;
}

.nav-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
  color: white;
}

.nav-item.active {
  background-color: #3b82f6;
  color: white;
}

.nav-item i {
  font-size: 1.1rem;
  width: 1.5rem;
  text-align: center;
}

.nav-label {
  white-space: nowrap;
}

.main-content {
  flex: 1;
  padding: 1.5rem;
  overflow-y: auto;
}

.sidebar.collapsed .nav-item {
  justify-content: center;
  padding: 0.75rem;
}

.sidebar.collapsed .sidebar-header {
  justify-content: center;
}
</style>
