<template>
  <q-page class="flex flex-center">
    <img
      alt="Quasar logo"
      src="~assets/quasar-logo-vertical.svg"
      style="width: 200px; height: 200px"
    />
  </q-page>
</template>

<script setup>
import { onMounted } from 'vue'
import { api } from 'boot/axios'
import { useQuasar } from 'quasar'

const $q = useQuasar()

onMounted(() => {
  api.get('/employees/').catch(err => {
    if (err.response?.status !== 401) $q.notify({ color: 'negative', message: 'Бэкенд недоступен!' })
  })
})
</script>
