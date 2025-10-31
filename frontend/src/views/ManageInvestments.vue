<template>
  <div class="manage-investments-container">
    <!-- Page Header -->
    <div class="page-header d-flex justify-content-between align-items-center">
      <div>
        <h1 class="page-title">
          <i class="bi bi-gear-fill me-2"></i>
          Manage Investment Products
        </h1>
        <p class="page-subtitle">Admin Panel - Create, Edit & Deactivate Products</p>
      </div>
      <button class="btn btn-primary btn-lg" @click="openCreateModal">
        <i class="bi bi-plus-circle me-2"></i>
        Add New Product
      </button>
    </div>

    <!-- Success/Error Alerts -->
    <div v-if="successMessage" class="alert alert-success alert-dismissible fade show" role="alert">
      <i class="bi bi-check-circle-fill me-2"></i>
      {{ successMessage }}
      <button type="button" class="btn-close" @click="successMessage = ''"></button>
    </div>

    <div v-if="errorMessage" class="alert alert-danger alert-dismissible fade show" role="alert">
      <i class="bi bi-exclamation-triangle-fill me-2"></i>
      {{ errorMessage }}
      <button type="button" class="btn-close" @click="errorMessage = ''"></button>
    </div>

    <!-- Loading State -->
    <div v-if="loading && !showModal" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
      <p class="mt-3 text-muted">Loading investment products...</p>
    </div>

    <!-- Investment Products Table -->
    <div v-else class="table-container">
      <div v-if="investments.length === 0" class="empty-state text-center py-5">
        <i class="bi bi-inbox display-1 text-muted"></i>
        <h3 class="mt-3">No Investment Products</h3>
        <p class="text-muted">Start by creating your first investment product.</p>
        <button class="btn btn-primary mt-3" @click="openCreateModal">
          <i class="bi bi-plus-circle me-2"></i>
          Create First Product
        </button>
      </div>

      <div v-else class="table-responsive">
        <table class="table table-hover align-middle">
          <thead class="table-dark">
            <tr>
              <th scope="col">ID</th>
              <th scope="col">Product Name</th>
              <th scope="col">Type</th>
              <th scope="col">Risk Level</th>
              <th scope="col" class="text-end">Min Investment</th>
              <th scope="col" class="text-end">Expected Return</th>
              <th scope="col" class="text-end">Current NAV</th>
              <th scope="col" class="text-center">Status</th>
              <th scope="col" class="text-center">Actions</th>
            </tr>
          </thead>
          <tbody>
            <ProductTableRow
              v-for="investment in investments"
              :key="investment.id"
              :investment="investment"
              @edit="openEditModal"
              @deactivate="confirmDeactivate"
              @activate="activateInvestment"
            />
          </tbody>
        </table>
      </div>
    </div>

    <!-- Create/Edit Modal -->
    <div
      v-if="showModal"
      class="modal fade show d-block"
      tabindex="-1"
      role="dialog"
      style="background-color: rgba(0, 0, 0, 0.5)"
    >
      <div class="modal-dialog modal-lg modal-dialog-centered" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              <i class="bi" :class="isEditMode ? 'bi-pencil-square' : 'bi-plus-circle'"></i>
              {{ isEditMode ? 'Edit Investment Product' : 'Create New Investment Product' }}
            </h5>
            <button type="button" class="btn-close" @click="closeModal"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="handleSubmit">
              <div class="row g-3">
                <!-- Product Name -->
                <div class="col-md-12">
                  <label for="name" class="form-label">
                    Product Name <span class="text-danger">*</span>
                  </label>
                  <input
                    id="name"
                    v-model="formData.name"
                    type="text"
                    class="form-control"
                    :class="{ 'is-invalid': validationErrors.name }"
                    placeholder="e.g., Tech Growth Fund"
                    required
                  />
                  <div v-if="validationErrors.name" class="invalid-feedback">
                    {{ validationErrors.name }}
                  </div>
                </div>

                <!-- Type -->
                <div class="col-md-6">
                  <label for="type" class="form-label">
                    Type <span class="text-danger">*</span>
                  </label>
                  <select
                    id="type"
                    v-model="formData.type"
                    class="form-select"
                    :class="{ 'is-invalid': validationErrors.type }"
                    required
                  >
                    <option value="">Select Type</option>
                    <option value="STOCK">Stock</option>
                    <option value="MUTUAL_FUND">Mutual Fund</option>
                    <option value="BOND">Bond</option>
                    <option value="ETF">ETF</option>
                    <option value="REAL_ESTATE">Real Estate</option>
                    <option value="COMMODITY">Commodity</option>
                    <option value="CRYPTOCURRENCY">Cryptocurrency</option>
                  </select>
                  <div v-if="validationErrors.type" class="invalid-feedback">
                    {{ validationErrors.type }}
                  </div>
                </div>

                <!-- Risk Level -->
                <div class="col-md-6">
                  <label for="riskLevel" class="form-label">
                    Risk Level <span class="text-danger">*</span>
                  </label>
                  <select
                    id="riskLevel"
                    v-model="formData.riskLevel"
                    class="form-select"
                    :class="{ 'is-invalid': validationErrors.riskLevel }"
                    required
                  >
                    <option value="">Select Risk Level</option>
                    <option value="LOW">Low</option>
                    <option value="MEDIUM">Medium</option>
                    <option value="HIGH">High</option>
                  </select>
                  <div v-if="validationErrors.riskLevel" class="invalid-feedback">
                    {{ validationErrors.riskLevel }}
                  </div>
                </div>

                <!-- Minimum Investment -->
                <div class="col-md-6">
                  <label for="minInvestment" class="form-label">
                    Minimum Investment (₹) <span class="text-danger">*</span>
                  </label>
                  <input
                    id="minInvestment"
                    v-model.number="formData.minInvestment"
                    type="number"
                    step="0.01"
                    class="form-control"
                    :class="{ 'is-invalid': validationErrors.minInvestment }"
                    placeholder="e.g., 1000.00"
                    required
                  />
                  <div v-if="validationErrors.minInvestment" class="invalid-feedback">
                    {{ validationErrors.minInvestment }}
                  </div>
                </div>

                <!-- Expected Return Rate -->
                <div class="col-md-6">
                  <label for="expectedReturnRate" class="form-label">
                    Expected Return Rate (%) <span class="text-danger">*</span>
                  </label>
                  <input
                    id="expectedReturnRate"
                    v-model.number="formData.expectedReturnRate"
                    type="number"
                    step="0.01"
                    class="form-control"
                    :class="{ 'is-invalid': validationErrors.expectedReturnRate }"
                    placeholder="e.g., 8.5"
                    required
                  />
                  <div v-if="validationErrors.expectedReturnRate" class="invalid-feedback">
                    {{ validationErrors.expectedReturnRate }}
                  </div>
                </div>

                <!-- Current NAV -->
                <div class="col-md-6">
                  <label for="currentNAV" class="form-label">
                    Current NAV (₹) <span class="text-danger">*</span>
                  </label>
                  <input
                    id="currentNAV"
                    v-model.number="formData.currentNAV"
                    type="number"
                    step="0.01"
                    class="form-control"
                    :class="{ 'is-invalid': validationErrors.currentNAV }"
                    placeholder="e.g., 100.00"
                    required
                  />
                  <div v-if="validationErrors.currentNAV" class="invalid-feedback">
                    {{ validationErrors.currentNAV }}
                  </div>
                </div>

                <!-- Is Active -->
                <div class="col-md-6">
                  <label for="isActive" class="form-label">Status</label>
                  <div class="form-check form-switch mt-2">
                    <input
                      id="isActive"
                      v-model="formData.isActive"
                      class="form-check-input"
                      type="checkbox"
                      role="switch"
                    />
                    <label class="form-check-label" for="isActive">
                      {{ formData.isActive ? 'Active' : 'Inactive' }}
                    </label>
                  </div>
                </div>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeModal">
              Cancel
            </button>
            <button
              type="button"
              class="btn btn-primary"
              @click="handleSubmit"
              :disabled="loading"
            >
              <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
              {{ isEditMode ? 'Update Product' : 'Create Product' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Confirmation Modal for Deactivation -->
    <div
      v-if="showConfirmModal"
      class="modal fade show d-block"
      tabindex="-1"
      role="dialog"
      style="background-color: rgba(0, 0, 0, 0.5)"
    >
      <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
          <div class="modal-header bg-warning">
            <h5 class="modal-title">
              <i class="bi bi-exclamation-triangle-fill me-2"></i>
              Confirm Deactivation
            </h5>
            <button type="button" class="btn-close" @click="showConfirmModal = false"></button>
          </div>
          <div class="modal-body">
            <p>
              Are you sure you want to deactivate
              <strong>{{ investmentToDeactivate?.name }}</strong>?
            </p>
            <p class="text-muted small">
              This product will no longer be visible to users.
            </p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showConfirmModal = false">
              Cancel
            </button>
            <button
              type="button"
              class="btn btn-warning"
              @click="deactivateInvestment"
              :disabled="loading"
            >
              <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
              Deactivate
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, reactive } from 'vue'
import { useInvestmentStore } from '@/stores/investmentStore'
import ProductTableRow from '@/components/ProductTableRow.vue'

export default {
  name: 'ManageInvestments',

  components: {
    ProductTableRow
  },

  setup() {
    // Store
    const investmentStore = useInvestmentStore()

    // Local State
    const showModal = ref(false)
    const showConfirmModal = ref(false)
    const isEditMode = ref(false)
    const successMessage = ref('')
    const errorMessage = ref('')
    const investmentToDeactivate = ref(null)

    const formData = reactive({
      id: null,
      name: '',
      type: '',
      riskLevel: '',
      minInvestment: 0,
      expectedReturnRate: 0,
      currentNAV: 0,
      isActive: true,
    })

    const validationErrors = reactive({
      name: '',
      type: '',
      riskLevel: '',
      minInvestment: '',
      expectedReturnRate: '',
      currentNAV: '',
    })

    // Computed Properties from Store
    const investments = computed(() => investmentStore.investments)
    const loading = computed(() => investmentStore.loading)

    // Methods

    // Load all investments (including inactive)
    async function loadInvestments() {
      try {
        await investmentStore.fetchInvestments()
      } catch (err) {
        errorMessage.value = 'Failed to load investments. Please try again.'
        console.error('Failed to load investments:', err)
      }
    }

    // Open modal for creating new investment
    function openCreateModal() {
      isEditMode.value = false
      resetForm()
      showModal.value = true
    }

    // Open modal for editing existing investment
    // @param {Object} investment - Investment to edit
    function openEditModal(investment) {
      isEditMode.value = true
      Object.assign(formData, {
        id: investment.id,
        name: investment.name,
        type: investment.type,
        riskLevel: investment.riskLevel,
        minInvestment: investment.minInvestment,
        expectedReturnRate: investment.expectedReturnRate,
        currentNAV: investment.currentNAV,
        isActive: investment.isActive,
      })
      showModal.value = true
    }

    // Close modal and reset form
    function closeModal() {
      showModal.value = false
      resetForm()
    }

    // Reset form data and validation errors
    function resetForm() {
      Object.assign(formData, {
        id: null,
        name: '',
        type: '',
        riskLevel: '',
        minInvestment: 0,
        expectedReturnRate: 0,
        currentNAV: 0,
        isActive: true,
      })
      resetValidationErrors()
    }

    // Reset validation errors
    function resetValidationErrors() {
      Object.keys(validationErrors).forEach((key) => {
        validationErrors[key] = ''
      })
    }

    // Validate form data
    // @returns {boolean} - Whether form is valid
    function validateForm() {
      resetValidationErrors()
      let isValid = true

      if (!formData.name || formData.name.trim() === '') {
        validationErrors.name = 'Product name is required'
        isValid = false
      }

      if (!formData.type) {
        validationErrors.type = 'Type is required'
        isValid = false
      }

      if (!formData.riskLevel) {
        validationErrors.riskLevel = 'Risk level is required'
        isValid = false
      } else if (!['LOW', 'MEDIUM', 'HIGH'].includes(formData.riskLevel)) {
        validationErrors.riskLevel = 'Risk level must be low, medium, or high'
        isValid = false
      }

      if (formData.minInvestment <= 0) {
        validationErrors.minInvestment = 'Minimum investment must be greater than 0'
        isValid = false
      }

      if (formData.expectedReturnRate <= 0) {
        validationErrors.expectedReturnRate = 'Expected return rate must be greater than 0'
        isValid = false
      }

      if (formData.currentNAV <= 0) {
        validationErrors.currentNAV = 'Current NAV must be greater than 0'
        isValid = false
      }

      return isValid
    }

    // Handle form submission (create or update)
    async function handleSubmit() {
      if (!validateForm()) {
        return
      }

      const confirmed = confirm(`Are you sure you want to ${isEditMode.value ? 'update' : 'create'} this investment product?`)
      if (!confirmed) {
        return
      }

      try {

        if (isEditMode.value) {
          await investmentStore.updateInvestment(formData.id, formData)
          successMessage.value = `Successfully updated "${formData.name}"`
        } else {
          await investmentStore.createInvestment(formData)
          successMessage.value = `Successfully created "${formData.name}"`
        }
        closeModal()
        await loadInvestments()

        // Clear success message after 5 seconds
        setTimeout(() => {
          successMessage.value = ''
        }, 5000)
      } catch (err) {
        errorMessage.value = err.response?.data?.message ||
          `Failed to ${isEditMode.value ? 'update' : 'create'} investment. Please try again.`
        console.error('Form submission error:', err)
      }
    }

    // Open confirmation modal for deactivation
    // @param {Object} investment - Investment to deactivate
    function confirmDeactivate(investment) {
      investmentToDeactivate.value = investment
      showConfirmModal.value = true
    }

    // Deactivate investment product
    async function deactivateInvestment() {
      try {
        await investmentStore.toggleActiveStatus(investmentToDeactivate.value.id, false)
        successMessage.value = `Successfully deactivated "${investmentToDeactivate.value.name}"`
        showConfirmModal.value = false
        investmentToDeactivate.value = null
        await loadInvestments()

        // Clear success message after 5 seconds
        setTimeout(() => {
          successMessage.value = ''
        }, 5000)
      } catch (err) {
        errorMessage.value = 'Failed to deactivate investment. Please try again.'
        console.error('Deactivation error:', err)
        showConfirmModal.value = false
      }
    }

    // Activate investment product
    // @param {Object} investment - Investment to activate
    async function activateInvestment(investment) {
      try {
        await investmentStore.toggleActiveStatus(investment.id, true)
        successMessage.value = `Successfully activated "${investment.name}"`
        await loadInvestments()

        // Clear success message after 5 seconds
        setTimeout(() => {
          successMessage.value = ''
        }, 5000)
      } catch (err) {
        errorMessage.value = 'Failed to activate investment. Please try again.'
        console.error('Activation error:', err)
      }
    }

    // Lifecycle Hooks
    onMounted(() => {
      loadInvestments()
    })

    return {
      // State
      showModal,
      showConfirmModal,
      isEditMode,
      successMessage,
      errorMessage,
      investmentToDeactivate,
      formData,
      validationErrors,
      // Computed
      investments,
      loading,
      // Methods
      loadInvestments,
      openCreateModal,
      openEditModal,
      closeModal,
      resetForm,
      handleSubmit,
      confirmDeactivate,
      deactivateInvestment,
      activateInvestment,
    }
  },
}
</script>

<style scoped>
.manage-investments-container {
  padding: 2rem 1.5rem;
  max-width: 1600px;
  margin: 0 auto;
  min-height: calc(100vh - 200px);
  background: #f8f9fa;
}

.page-header {
  margin-bottom: 2rem;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.page-subtitle {
  font-size: 1.1rem;
  color: #6c757d;
  margin-bottom: 0;
}

.table-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.table {
  margin-bottom: 0;
}

.table thead {
  background-color: #2c3e50;
  color: white;
}

.empty-state i {
  font-size: 4rem;
}

.modal.show {
  display: block;
}

.form-label {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.form-control:focus,
.form-select:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
}

/* Alert animations */
.alert {
  animation: slideInDown 0.3s ease-out;
}

@keyframes slideInDown {
  from {
    transform: translateY(-20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

/* Responsive Design */
@media (max-width: 768px) {
  .manage-investments-container {
    padding: 1.5rem 1rem;
  }

  .page-title {
    font-size: 1.75rem;
  }

  .page-subtitle {
    font-size: 1rem;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start !important;
    gap: 1rem;
  }

  .page-header .btn {
    width: 100%;
  }

  .table-responsive {
    font-size: 0.85rem;
    overflow-x: auto;
  }

  .btn-group .btn {
    padding: 0.25rem 0.5rem;
    font-size: 0.875rem;
  }

  .modal-dialog {
    margin: 1rem;
  }
}
</style>
