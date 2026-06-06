<template>
  <q-page class="q-pa-md">
    <div class="row justify-between items-center q-mb-md">
      <div class="text-h5 text-weight-bold text-secondary">Список сотрудников</div>
      <q-btn color="primary" icon="add" label="Добавить сотрудника" @click="openCreateDialog" />
    </div>

    <q-table
      :rows="employees"
      :columns="columns"
      row-key="id"
      flat
      bordered
      :loading="loading"
    >
      <template v-slot:body-cell-actions="props">
        <q-td :props="props" class="q-gutter-xs">
          <q-btn size="sm" color="info" icon="edit" round @click="openEditDialog(props.row)" />
          <q-btn size="sm" color="negative" icon="delete" round @click="deleteEmployee(props.row.id)" />
        </q-td>
      </template>
    </q-table>

    <!-- Диалог создания -->
    <q-dialog v-model="showCreateDialog">
      <q-card style="width: 500px">
        <q-card-section><div class="text-h6">Новый сотрудник</div></q-card-section>
        <q-card-section class="q-gutter-md">
          <q-input v-model="newEmp.firstName" label="Имя" outlined dense />
          <q-input v-model="newEmp.lastName" label="Фамилия" outlined dense />
          <q-input v-model="newEmp.position" label="Должность" outlined dense />
          <q-input v-model="newEmp.department" label="Отдел" outlined dense />
        </q-card-section>
        <q-card-actions align="right">
          <q-btn flat label="Отмена" v-close-popup />
          <q-btn color="primary" label="Сохранить" @click="createEmployee" />
        </q-card-actions>
      </q-card>
    </q-dialog>

    <!-- Диалог редактирования -->
    <q-dialog v-model="showEditDialog">
      <q-card style="width: 500px">
        <q-card-section><div class="text-h6">Редактирование сотрудника</div></q-card-section>
        <q-card-section class="q-gutter-md">
          <q-input v-model="editEmp.firstName" label="Имя" outlined dense />
          <q-input v-model="editEmp.lastName" label="Фамилия" outlined dense />
          <q-input v-model="editEmp.position" label="Должность" outlined dense />
          <q-input v-model="editEmp.department" label="Отдел" outlined dense />
        </q-card-section>
        <q-card-actions align="right">
          <q-btn flat label="Отмена" v-close-popup />
          <q-btn color="primary" label="Обновить" @click="updateEmployee" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { api } from 'boot/axios'
import { useQuasar } from 'quasar'

const $q = useQuasar()
const employees = ref([])
const loading = ref(false)
const showCreateDialog = ref(false)
const showEditDialog = ref(false)

const newEmp = ref({ firstName: '', lastName: '', position: '', department: '' })
const editEmp = ref({ id: null, firstName: '', lastName: '', position: '', department: '' })

const columns = [
  { name: 'id', label: 'ID', field: 'id', align: 'left', sortable: true },
  { name: 'lastName', label: 'Фамилия', field: 'lastName', align: 'left', sortable: true },
  { name: 'firstName', label: 'Имя', field: 'firstName', align: 'left' },
  { name: 'position', label: 'Должность', field: 'position', align: 'left' },
  { name: 'department', label: 'Отдел', field: 'department', align: 'left' },
  { name: 'hireDate', label: 'Дата найма', field: 'hireDate', align: 'left' },
  { name: 'actions', label: 'Действия', align: 'center' }
]

const fetchEmployees = async () => {
  loading.value = true
  try {
    const response = await api.get('/employees/')
    employees.value = response.data
  } catch {
    $q.notify({ color: 'negative', message: 'Ошибка загрузки данных' })
  } finally {
    loading.value = false
  }
}

const openCreateDialog = () => {
  newEmp.value = { firstName: '', lastName: '', position: '', department: '' }
  showCreateDialog.value = true
}

const createEmployee = async () => {
  try {
    await api.post('/employees/', newEmp.value)
    showCreateDialog.value = false
    $q.notify({ color: 'positive', message: 'Сотрудник добавлен!' })
    fetchEmployees()
  } catch {
    $q.notify({ color: 'negative', message: 'Не удалось добавить сотрудника' })
  }
}

const openEditDialog = (employee) => {
  editEmp.value = { ...employee } // копируем, чтобы не мутировать оригинал
  showEditDialog.value = true
}

const updateEmployee = async () => {
  try {
    await api.put(`/employees/${editEmp.value.id}`, editEmp.value)
    showEditDialog.value = false
    $q.notify({ color: 'positive', message: 'Данные обновлены!' })
    fetchEmployees()
  } catch {
    $q.notify({ color: 'negative', message: 'Ошибка обновления' })
  }
}

const deleteEmployee = async (id) => {
  $q.dialog({
    title: 'Подтверждение',
    message: 'Вы уверены, что хотите удалить сотрудника?',
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await api.delete(`/employees/${id}`)
      $q.notify({ color: 'positive', message: 'Сотрудник удален' })
      fetchEmployees()
    } catch {
      $q.notify({ color: 'negative', message: 'Ошибка удаления' })
    }
  })
}

onMounted(() => {
  fetchEmployees()
})
</script>
