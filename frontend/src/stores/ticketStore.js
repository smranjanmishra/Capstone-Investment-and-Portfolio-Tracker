import { defineStore } from "pinia";
import { ref } from 'vue';
import {
  createTicket as apiCreateTicket,
  getUserTickets as apiGetUserTickets,
  getAllTickets as apiGetAllTickets,
  respondToTicket as apiRespondToTicket,
} from '@/services/api';
import { isAdmin } from '@/utils/auth';

export const useTicketStore = defineStore('ticket', () => {

  // --- STATE ---
  const userTickets = ref([]);
  const allTickets = ref([]);
  const currentTicket = ref(null);
  const loading = ref(false);
  const error = ref(null);

  //Funtion to create Ticket using Ticket Data
  async function createTicket(ticketData) {
    loading.value = true;
    error.value = null;
    try {
      // Validate & normalize payload (do not convert investmentProductId to Number)
      const subject = ticketData.subject ? String(ticketData.subject).trim() : ''
      const description = ticketData.description ? String(ticketData.description).trim() : ''
      const investmentProductId = ticketData.investmentProductId == null || ticketData.investmentProductId === '' ? null : ticketData.investmentProductId

      if (!subject || !description) {
        error.value = 'Subject and description cannot be blank or whitespace only.'
        throw new Error(error.value)
      }

      const payload = {
        subject,
        description,
        investmentProductId
      }

      const response = await apiCreateTicket(payload);
      await fetchUserTickets();
      console.log('Ticket created successfully:', response.data);
      return response.data;
    }
    catch (error) {
      error.value = error.response?.data || error.message || 'Failed to create ticket';
      console.error('Error creating ticket:', error);
      throw error;
    }
    finally {
      loading.value = false;
    }
  }
//Function to fetch all the tickets created by the user 
  async function fetchUserTickets() {
    loading.value = true;
    error.value = null;
    try {
      
      const response = await apiGetUserTickets();
      userTickets.value = response.data;
      console.log(`Loaded ${userTickets.value.length} user tickets`);
      return userTickets.value;
    }
    catch (error) {
      error.value = error.response?.data || 'Failed to fetch user tickets';
      console.error('Error fetching user tickets:', error);
      throw error;
    }
    finally {
      loading.value = false;
    }
  }
//Function to fetch All Tickets (Admin only)
  async function fetchAllTickets() {
    loading.value = true;
    error.value = null;
    try {
      const response = await apiGetAllTickets();
      allTickets.value = response.data;
      console.log(`Loaded ${allTickets.value.length} total tickets for admin`);
      return allTickets.value;
    }
    catch (error) {
      error.value = error.response?.data || 'Failed to fetch user tickets';
      console.error('Error fetching user tickets:', error);
      throw error;
    }
    finally {
      loading.value = false;
    }
  }
//Function to respond to Ticket (Admin only )
  async function respondToTicket(ticketId, responseText, newPriority) {
  loading.value = true;
  error.value = null;

  try {

    const trimmedResponse = responseText ? String(responseText).trim() : ''
    if (!trimmedResponse) {
      error.value = 'Response/Comment cannot be empty.'
      throw new Error(error.value)
    }

    const currentStatus = currentTicket.value?.ticketStatus;
    let updatedStatus = currentStatus;
    if (currentStatus === "OPEN") {
      updatedStatus = "RESPONDED";
    } else if (currentStatus === "RESPONDED") {
      updatedStatus = "CLOSED";
    }

    await apiRespondToTicket(ticketId, {
      response: trimmedResponse,
      priority: newPriority
    });
    console.log("Ticket updated — refreshing data...");
    await fetchAllTickets();
    await fetchUserTickets();
    await fetchTicketById(ticketId);
    return currentTicket.value;
  }
  catch (err) {
    error.value = err.response?.data || err.message || 'Failed to update ticket';
    console.error("Error updating ticket:", err);
    throw err;
  }
  finally {
    loading.value = false;
  }
}

//Function to fetch tickets by Ticket Id in order to retrive details of the ticket 
  async function fetchTicketById(ticketId) {
    loading.value = true;
    error.value = null;
    try {
      let ticket = userTickets.value.find(t => t.id === ticketId);
      if (!ticket) {
        ticket = allTickets.value.find(t => t.id === ticketId);
      }
      
      if (ticket) {
        currentTicket.value = ticket;
        return ticket;
      } else {
        // Fetch tickets if not in cache
        if (isAdmin()) {
          await fetchAllTickets();
          ticket = allTickets.value.find(t => t.id === ticketId);
        } else {
          await fetchUserTickets();
          ticket = userTickets.value.find(t => t.id === ticketId);
        }
        
        if (ticket) {
          currentTicket.value = ticket;
          return ticket;
        } else {
           throw new Error('Ticket not found or access denied.');
        }
      }
    } catch (err) {
      error.value = err.message || 'Failed to fetch ticket details';
      console.error('Error fetching ticket by ID:', err);
      currentTicket.value = null;
      throw err;
    } finally {
      loading.value = false;
    }
  } 

  return {
    // State
    userTickets,
    allTickets,
    currentTicket,
    loading,
    error,
    // Actions
    createTicket,
    fetchUserTickets,
    fetchAllTickets,
    respondToTicket,
    fetchTicketById,
    
  };
});
