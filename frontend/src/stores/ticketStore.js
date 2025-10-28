import { defineStore } from "pinia";
import { ref } from 'vue';
import apiClient, {
  createTicket as apiCreateTicket,
  getUserTickets as apiGetUserTickets,
  getAllTickets as apiGetAllTickets,
  respondToTicket as apiRespondToTicket,
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
  async function respondToTicket(ticketId, responseText,newPriority) {
    loading.value = true;
    error.value = null;
    try {
      const response = await apiRespondToTicket(ticketId, {
        response: responseText,
        priority: newPriority
      });
      const updatedTicketData = response.data;
      if (!updatedTicketData || !updatedTicketData.id) {
          throw new Error("Invalid response received from server after update.");
      }
      const ticketIdToUpdate = updatedTicketData.id;

      // Update allTickets list
      const indexAll = allTickets.value.findIndex(t => t.id === ticketIdToUpdate);
      if (indexAll !== -1) {
        allTickets.value[indexAll] = updatedTicketData; 
      }
      const indexUser = userTickets.value.findIndex(t => t.id === ticketIdToUpdate);
       if (indexUser !== -1) {
         userTickets.value[indexUser] = updatedTicketData; 
      }
      if (currentTicket.value && currentTicket.value.id === ticketIdToUpdate) {
        currentTicket.value = updatedTicketData; 
      }
      if (updatedTicketData.ticketStatus === 'CLOSED') {
         console.log('Ticket closed by admin successfully:', updatedTicketData);
      } else {
         console.log('Ticket responded to successfully:', updatedTicketData);
      }
      return updatedTicketData; 
    } 
    catch (err) {
      error.value = err.response?.data?.message || err.response?.data || err.message || 'Failed to update ticket';
      console.error('Error updating ticket (respond/close):', err);
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
    
  };
});
