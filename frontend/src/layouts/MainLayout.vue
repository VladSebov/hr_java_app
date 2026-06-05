<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated>
      <q-toolbar>
        <q-btn flat dense round icon="menu" aria-label="Menu" @click="toggleLeftDrawer" />

        <q-toolbar-title> HR Java App with Quasar Frontend</q-toolbar-title>

        <div>Quasar v{{ $q.version }}</div>
        <q-btn flat round icon="logout" @click="handleLogout" />
      </q-toolbar>
    </q-header>

    <q-drawer v-model="leftDrawerOpen" show-if-above bordered>
      <q-list>
        <q-item-label header>Навигация</q-item-label>

        <EssentialLink
          v-for="link in linksList"
          :key="link.title"
          v-bind="link"
        />
      </q-list>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import EssentialLink from 'components/EssentialLink.vue'

const router = useRouter()
const leftDrawerOpen = ref(false)

function toggleLeftDrawer() {
  leftDrawerOpen.value = !leftDrawerOpen.value
}

const linksList = [
  {
    title: 'Сотрудники',
    caption: 'Управление сотрудниками',
    icon: 'people',
    link: '/employees'
  },
  {
    title: 'Пользователи',
    caption: 'Управление пользователями',
    icon: 'person',
    link: '/users'
  }
]

const handleLogout = () => {
  localStorage.removeItem('jwt_token')
  localStorage.removeItem('user_role')
  router.push('/login')
}
</script>
