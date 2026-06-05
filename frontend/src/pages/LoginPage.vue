<template>
  <q-layout view="lHh Lpr lFf">
    <q-page-container>

      <q-page class="flex flex-center bg-grey-2">
        <q-card class="q-pa-md shadow-2" style="width: 400px">
          <q-card-section class="text-center">
            <div class="text-h5 text-primary text-weight-bold">HR Java App</div>
            <div class="text-subtitle2 text-grey-6">Войдите в систему</div>
          </q-card-section>

          <q-card-section>
            <q-form @submit="onLogin" class="q-gutter-md">
              <q-input v-model="form.username" label="Логин" outlined required dense />
              <q-input v-model="form.password" label="Пароль" type="password" outlined required dense />

              <div class="q-mt-lg">
                <q-btn label="Войти" type="submit" color="primary" class="full-width" />
              </div>
            </q-form>
          </q-card-section>
        </q-card>
      </q-page>

    </q-page-container>
  </q-layout>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { api } from 'boot/axios'
import { useQuasar } from 'quasar'

const $q = useQuasar()
const router = useRouter()
const form = ref({ username: '', password: '' })

const onLogin = async () => {
  try {

    const response = await api.post('/auth/login', {
      username: form.value.username,
      password: form.value.password
    })

    localStorage.setItem('jwt_token', response.data.token)
    localStorage.setItem('user_role', response.data.role)

    $q.notify({ color: 'positive', message: 'Успешный вход!', icon: 'check' })
    router.push('/employees')
  } catch {
    $q.notify({ color: 'negative', message: 'Неверный логин или пароль', icon: 'error' })
  }
}
</script>
