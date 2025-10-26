import { defineStore } from "pinia";
import {ref} from vue;
import apiClient from '@/services/api'

export const useTicketStore=defineStore('ticket',()=>{

    // --- STATE ---
  // For a user's own tickets
  const userTickets = ref([]);
  // For an admin viewing all tickets
  const allTickets = ref([]);
  // For viewing a single ticket's details
  const currentTicket = ref(null);
  const loading = ref(false);
  const error = ref(null);

  //---API Calls-----//
  //(USER) Creates a new support ticket.
  //{Object} ticketData - { subject, description, investmentProductId }

  async function createTickets(ticketData) {
    loading.value=true;
    error.value=null;
    try {
    // POST /api/v1/support
      const response = await apiClient.post('/support', ticketData);
      // Add the new ticket to the user's list (if we get it back)
      // The backend currently returns a string, so we  need to re-fetch
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
      loading.value = false
    }
  }

  //USER Fetches all tickets for the currently logged-in user.
  async function fetchUserTickets() {
    loading.value=true;
    error.value=null;
    try {
    // GET /api/v1/support/user
      const response = await apiClient.get('/support/user');
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
      loading.value = false
    }
  } 

  //(ADMIN) Fetches all tickets from all users.
  async function fetchAllTickets() {
    loading.value = true
    error.value = null
    try {
      // GET /api/v1/admin/support
      const response = await apiClient.get('/admin/support');
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
      loading.value = false
    }
  }

  // (ADMIN) Responds to a specific ticket.
  // {Number} ticketId - The ID of the ticket to respond to.
  // {String} responseText - The admin's response.

async function respondToTicket(ticketId, responseText) {
    loading.value = true
    error.value = null
    try {
      // PUT /api/v1/support/{ticketId}/respond
      const response = await apiClient.put(`/support/${ticketId}/respond`, {
        response: responseText
      })
      
      // Update the ticket in the local 'allTickets' list
      const index = allTickets.value.findIndex(t => t.id === ticketId)
      if (index !== -1) {
        allTickets.value[index].status = 'RESPONDED'
        allTickets.value[index].response = responseText
        // We might need to refresh the data from the server if the backend updates more fields
      }
      
      // Also update currentTicket if it's the one being viewed
      if (currentTicket.value && currentTicket.value.id === ticketId) {
        currentTicket.value.status = 'RESPONDED'
        currentTicket.value.response = responseText
      }

      console.log('Ticket responded to successfully:', response.data)
      return response.data
    } catch (err) {
      error.value = err.response?.data || 'Failed to respond to ticket'
      console.error('Error responding to ticket:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

// Fetches a single ticket by its ID.
// (Used by both user and admin to see details)
  async function fetchTicketById(ticketId) {
    loading.value = true
    error.value = null
    try {
      // We don't have a GET /api/v1/support/{id} endpoint.
      // So, we'll find it from the lists we already fetched.
      // This is much faster.
      
      let ticket = userTickets.value.find(t => t.id === ticketId)
      if (!ticket) {
        ticket = allTickets.value.find(t => t.id === ticketId)
      }
      
      if (ticket) {
        currentTicket.value = ticket
        return ticket
      } else {
        
        // If not found, it might be an admin looking at a user's ticket.
        // Or a user looking at an old ticket.
        // For simplicity, we'll just re-fetch all relevant tickets.
        // A dedicated backend endpoint would be better, but we work with what we have.

        if (localStorage.getItem('currentUser')) {
           const userRole = JSON.parse(localStorage.getItem('currentUser')).role
           if (userRole === 'ADMIN') {
             await fetchAllTickets()
             ticket = allTickets.value.find(t => t.id === ticketId)
           } else {
             await fetchUserTickets()
             ticket = userTickets.value.find(t => t.id === ticketId)
           }
        }
        
        if (ticket) {
          currentTicket.value = ticket
          return ticket
        } else {
           throw new Error('Ticket not found or access denied.')
        }
      }
    } catch (err) {
      error.value = err.message || 'Failed to fetch ticket details'
      console.error('Error fetching ticket by ID:', err)
      currentTicket.value = null
      throw err
    } finally {
      loading.value = false
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
    fetchTicketById
  }
})
