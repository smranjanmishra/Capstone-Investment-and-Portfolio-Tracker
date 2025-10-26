<template>
  <tr :class="txn.txnType === 'BUY' ? 'buy' : 'sell'">
    <td>{{ txn.id }}</td>
    <td>{{ txn.txnType }}</td>
    <td>{{ txn.units }}</td>
    <td>₹{{ txn.navAtTxn }}</td>
    <td>{{ new Date(txn.txnDate).toLocaleString() }}</td>
  </tr>
</template>

<script setup>
defineProps({ txn: Object });
</script>

<style scoped>
tr {
  transition: background 0.2s ease;
}

.buy {
  background: #e8f5e9;
}

.sell {
  background: #ffebee;
}

td {
  padding: 8px 12px;
  border-bottom: 1px solid #ddd;
  font-size: 0.9rem;
  color: #1e293b;
}

tr:hover {
  background: #f1f5f9;
}
</style>
