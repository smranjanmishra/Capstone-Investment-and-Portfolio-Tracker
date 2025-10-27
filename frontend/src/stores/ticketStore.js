import { defineStore } from "pinia";
import { ref } from 'vue';
import apiClient, {
  createTicket as apiCreateTicket,
  getUserTickets as apiGetUserTickets,
  getAllTickets as apiGetAllTickets,
  respondToTicket as apiRespondToTicket,
  closeTicket as apiCloseTicket
} from '@/services/api';

export const useTicketStore = defineStore('ticket', () => {

  // --- STATE ---
  const userTickets = ref([]);
  const allTickets = ref([]);
  const currentTicket = ref(null);
  const loading = ref(false);
  const error = ref(null);

  // --- API Calls --

  //Funtion to create Ticket using Ticket Data
  async function createTicket(ticketData) {
    loading.value = true;
    error.value = null;
    try {
      
      const response = await apiCreateTicket(ticketData);
      await fetchUserTickets();
      console.log('Ticket created successfully:', response.data);
      return response.data;
    }
    catch (error) {
      error.value = error.response?.data || 'Failed to create ticket';
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
  async function respondToTicket(ticketId, responseText) {
    loading.value = true;
    error.value = null;
    try {
      const response = await apiRespondToTicket(ticketId, {
        response: responseText
      });
      
      const index = allTickets.value.findIndex(t => t.id === ticketId);
      if (index !== -1) {
        allTickets.value[index].status = 'RESPONDED';
        allTickets.value[index].response = responseText;
      }
      
      if (currentTicket.value && currentTicket.value.id === ticketId) {
        currentTicket.value.status = 'RESPONDED';
        currentTicket.value.response = responseText;
      }

      console.log('Ticket responded to successfully:', response.data);
      return response.data;
    } catch (err) {
      error.value = err.response?.data || 'Failed to respond to ticket';
      console.error('Error responding to ticket:', err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  //Function to close the Ticket (User only )after getting response from the admin
  async function closeTicket(ticketId) {
    loading.value = true;
    error.value = null;
    try {
     
      const response = await apiCloseTicket(ticketId);
      const updatedStatus = response.data.ticketStatus || 'CLOSED';
      const userIndex = userTickets.value.findIndex(t => t.id === ticketId);
      
      if (userIndex !== -1) {
        userTickets.value[userIndex].status = updatedStatus;
        userTickets.value[userIndex].updatedAt = response.data.updatedAt;
      }

      if (currentTicket.value && currentTicket.value.id === ticketId) {
        currentTicket.value.status = updatedStatus;
        currentTicket.value.updatedAt = response.data.updatedAt;
      }
      
      console.log('Ticket closed successfully:', response.data);
      return response.data;

    } catch (err) {
      error.value = err.response?.data || 'Failed to close ticket';
      console.error('Error closing ticket:', err);
      throw err;
    } finally {
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
        if (localStorage.getItem('currentUser')) {
           const userRole = JSON.parse(localStorage.getItem('currentUser')).role;
           if (userRole === 'ADMIN') {
             await fetchAllTickets();
             ticket = allTickets.value.find(t => t.id === ticketId);
           } else {
             await fetchUserTickets();
             ticket = userTickets.value.find(t => t.id === ticketId);
           }
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
    closeTicket 
  };
});
