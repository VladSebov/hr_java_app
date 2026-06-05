<template>
  <q-page class="q-pa-md">
    <div class="row justify-between items-center q-mb-md">
      <div class="text-h4">Управление пользователями</div>
      <q-btn color="primary" label="Добавить пользователя" @click="openDialog()" />
    </div>

    <q-table :rows="users" :columns="columns" row-key="id">
      <template v-slot:body-cell-actions="props">
        <q-td :props="props">
          <q-btn icon="edit" flat color="blue" @click="openDialog(props.row)" />
          <q-btn icon="delete" flat color="red" @click="deleteUser(props.row.id)" />
        </q-td>
      </template>
    </q-table>

    <q-dialog v-model="showDialog">
      <q-card style="width: 400px">
        <q-card-section>
          <div class="text-h6">{{ isEdit ? 'Редактирование' : 'Новый пользователь' }}</div>
        </q-card-section>
        <q-card-section>
          <q-input v-model="form.username" label="Логин" />
          <q-input v-model="form.password" label="Пароль" type="password" />
          <q-select v-model="form.role" :options="['USER', 'ADMIN']" label="Роль" />
        </q-card-section>
        <q-card-actions>
          <q-btn label="Отмена" v-close-popup flat />
          <q-btn label="Сохранить" color="primary" @click="saveUser" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { api } from 'boot/axios'

const users = ref([])
const showDialog = ref(false)
const isEdit = ref(false)
const form = ref({ username: '', password: '', role: 'USER' })

const columns = [
  { name: 'username', label: 'Логин', field: 'username', align: 'left' },
  { name: 'role', label: 'Роль', field: 'role', align: 'left' },
  { name: 'actions', label: 'Действия', field: 'actions' }
]

const loadUsers = async () => {
  const response = await api.get('/users/')
  users.value = response.data
}

const openDialog = (user = null) => {
  isEdit.value = !!user
  form.value = user ? { ...user } : { username: '', password: '', role: 'USER' }
  showDialog.value = true
}

const saveUser = async () => {
  const token = localStorage.getItem('jwt_token');
  const config = { headers: { Authorization: `Bearer ${token}` } };

  if (isEdit.value) await api.put(`/users/${form.value.id}`, form.value, config);
  else await api.post('/users/', form.value, config);

  showDialog.value = false;
  loadUsers();
}

const deleteUser = async (id) => {
  if (confirm('Удалить пользователя?')) {
    await api.delete(`/users/${id}`)
    loadUsers()
  }
}

onMounted(loadUsers)
</script>
