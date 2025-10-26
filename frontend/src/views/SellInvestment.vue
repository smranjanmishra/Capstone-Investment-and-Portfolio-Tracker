<template>
  <div class="modal-overlay" @click.self="closeModal" role="dialog" aria-labelledby="modal-title" aria-modal="true">
    <div class="modal-container">
      <button class="close-btn" @click="closeModal" aria-label="Close modal">×</button>

      <h4 class="modal-title" id="modal-title">
        Sell {{ product?.investmentProductName || 'Investment' }}
      </h4>
      <p class="text-muted" v-if="product">
        Effective NAV:
        <strong>{{ formatCurrency(effectiveNAV) }}</strong>
      </p>

      <div v-if="loading" class="loader-box">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
        <p class="mt-2 text-muted">Loading investment...</p>
      </div>

      <div v-else-if="error" class="alert alert-danger" role="alert">
        {{ error }}
        <button class="btn btn-sm btn-outline-danger ms-3" @click="loadProduct" aria-label="Retry loading">
          Retry
        </button>
      </div>

      <div v-else-if="!product" class="empty-state text-center py-4">
        <i class="bi bi-exclamation-circle display-1 text-muted"></i>
        <h5 class="mt-3">Investment Not Found</h5>
        <p class="text-muted">The requested investment could not be found.</p>
        <button class="btn btn-primary mt-3" @click="closeModal" aria-label="Go back">
          Go Back
        </button>
      </div>

      <div v-else>
        <div class="info-box">
          <div>
            <span class="label">Units Available</span>
            <span class="value">{{ formatUnits(product.unitsOwned) }}</span>
          </div>
          <div>
            <span class="label">Current Value</span>
            <span class="value">{{ formatCurrency(product.unitsOwned * effectiveNAV) }}</span>
          </div>
        </div>

        <label class="form-label mt-3" for="units-input">Units to Sell</label>
        <input
          id="units-input"
          v-model="units"
          type="number"
          min="0.01"
          step="0.01"
          class="form-control"
          placeholder="Enter units (e.g., 10.00)"
          aria-describedby="units-error"
          @input="validate"
        />
        <small v-if="errorMsg" id="units-error" class="text-danger">{{ errorMsg }}</small>

        <div class="d-flex justify-content-between mt-2">
          <span>Sell Amount</span>
          <strong>{{ formatCurrency(units * effectiveNAV) }}</strong>
        </div>

        <button
          class="btn sell-btn w-100 mt-3"
          :disabled="!canSubmit || submitting"
          @click="confirmSell"
          aria-label="Confirm sale"
        >
          <span v-if="submitting" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
          {{ submitting ? "Processing..." : "Confirm Sale" }}
        </button>

        <p v-if="message" class="mt-3 text-center" :class="success ? 'text-success' : 'text-danger'">
          {{ message }}
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { getPortfolio, sellInvestment } from "@/services/api";

const route = useRoute();
const router = useRouter();
const productId = ref(Number(route.query.id) || null);

const product = ref(null);
const units = ref("");
const errorMsg = ref("");
const submitting = ref(false);
const message = ref("");
const success = ref(false);
const loading = ref(true);
const error = ref("");

// Fetch single investment product from API
async function fetchInvestment(id) {
  try {
    const response = await fetch(`http://localhost:8080/api/v1/investments`, {
      method: "GET",
      headers: { "Content-Type": "application/json" }
    });
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    const result = await response.json();
    if (!result.success || !Array.isArray(result.data)) {
      throw new Error(result.message || "Invalid investment data");
    }
    return result.data.find((inv) => inv.id === id) || null;
  } catch (e) {
    console.error("Failed to fetch investment:", e);
    return null;
  }
}

// Load product data
async function loadProduct() {
  loading.value = true;
  error.value = "";
  product.value = null;

  if (!Number.isFinite(productId.value) || productId.value <= 0) {
    error.value = "Invalid investment ID.";
    loading.value = false;
    return;
  }

  try {
    const [portfolioRes, investment] = await Promise.all([
      getPortfolio(),
      fetchInvestment(productId.value)
    ]);

    const list = portfolioRes?.data?.data || [];
    const portfolioItem = list.find((x) => Number(x.investmentProductId) === productId.value) || null;

    if (!portfolioItem && !investment) {
      error.value = "Investment not found.";
      loading.value = false;
      return;
    }

    product.value = {
      investmentProductId: productId.value,
      investmentProductName:
        investment?.name ||
        portfolioItem?.investmentProductName ||
        `Asset #${productId.value}`,
      unitsOwned: Number(portfolioItem?.unitsOwned) || 0,
      avgPurchasePrice: Number(portfolioItem?.avgPurchasePrice) || 0,
      currentNAV: Number.isFinite(Number(investment?.currentNAV))
        ? Number(investment.currentNAV)
        : Number.isFinite(Number(portfolioItem?.currentNAV))
          ? Number(portfolioItem.currentNAV)
          : 0
    };
  } catch (e) {
    error.value = e?.response?.data?.message || e?.message || "Failed to load investment.";
  } finally {
    loading.value = false;
  }
}
onMounted(loadProduct);

const effectiveNAV = computed(() => {
  const nav = Number(product.value?.currentNAV);
  const avg = Number(product.value?.avgPurchasePrice) || 0;
  return Number.isFinite(nav) && nav > 0 ? nav : avg;
});

function validate() {
  const u = Number(units.value);
  if (!Number.isFinite(u) || u <= 0) {
    errorMsg.value = "Enter a valid number of units (greater than 0).";
  } else if (u < 0.01) {
    errorMsg.value = "Units must be at least 0.01.";
  } else if (product.value && u > Number(product.value.unitsOwned)) {
    errorMsg.value = `Maximum ${formatUnits(product.value.unitsOwned)} units available.`;
  } else {
    errorMsg.value = "";
  }
}

const canSubmit = computed(() =>
  Number(units.value) > 0 &&
  Number(units.value) >= 0.01 &&
  product.value &&
  Number(units.value) <= Number(product.value.unitsOwned) &&
  !errorMsg.value &&
  !error.value
);

async function confirmSell() {
  if (!canSubmit.value) return;

  const confirmMessage = `Are you sure you want to sell ${formatUnits(units.value)} units of ${
    product.value.investmentProductName
  } for ${formatCurrency(Number(units.value) * effectiveNAV.value)}?`;
  if (!window.confirm(confirmMessage)) return;

  submitting.value = true;
  message.value = "";
  success.value = false;
  try {
    await sellInvestment({ investmentProductId: productId.value, units: Number(units.value) });
    success.value = true;
    message.value = "Sale successful!";
    setTimeout(() => closeModal(), 1000);
  } catch (e) {
    success.value = false;
    message.value = e?.response?.data?.message || "Sale failed.";
  } finally {
    submitting.value = false;
  }
}

function closeModal() {
  if (router.options.history.state.back) {
    router.back();
  } else {
    router.push("/portfolio");
  }
}

function formatCurrency(v) {
  return new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(Number(v) || 0);
}

function formatUnits(v) {
  return Number(v || 0).toLocaleString("en-IN", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  });
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.55);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1050;
}
.modal-container {
  background: #fff;
  border-radius: 12px;
  max-width: 520px;
  width: 92%;
  padding: 1.5rem;
  position: relative;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}
.close-btn {
  position: absolute;
  top: 0.75rem;
  right: 0.75rem;
  border: none;
  background: transparent;
  font-size: 1.5rem;
  line-height: 1;
  cursor: pointer;
  color: #666;
  transition: color 0.2s;
}
.close-btn:hover {
  color: #000;
}
.modal-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 0.5rem;
}
.text-muted {
  font-size: 0.9rem;
}
.info-box {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 8px;
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1rem;
}
.label {
  color: #69727a;
  font-size: 0.85rem;
  display: block;
}
.value {
  font-weight: 600;
  font-size: 0.95rem;
}
.loader-box {
  text-align: center;
  padding: 2rem 0;
}
.empty-state {
  background: #fff;
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}
.form-label {
  font-size: 0.9rem;
  font-weight: 600;
  color: #2c3e50;
}
.form-control {
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  padding: 0.75rem;
  font-size: 0.95rem;
}
.form-control:focus {
  border-color: #ff6b6b;
  box-shadow: 0 0 0 0.2rem rgba(255, 107, 107, 0.15);
}
.text-danger {
  font-size: 0.8rem;
  margin-top: 0.25rem;
  display: block;
}
.sell-btn {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
  color: #fff;
  border: none;
  padding: 0.875rem;
  border-radius: 8px;
  font-weight: 600;
  font-size: 0.9375rem;
  transition: 0.3s;
}
.sell-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}
.sell-btn:disabled {
  background: #e0e0e0;
  color: #999;
  cursor: not-allowed;
}
.alert {
  margin: 1rem 0;
  padding: 0.75rem;
  border-radius: 8px;
}
.alert-danger {
  background: #f8d7da;
  color: #721c24;
}
.btn-outline-danger {
  border-color: #721c24;
  color: #721c24;
}
@media (max-width: 576px) {
  .modal-container {
    width: 95%;
    padding: 1rem;
  }
  .info-box {
    flex-direction: column;
    gap: 0.5rem;
  }
}
</style>
