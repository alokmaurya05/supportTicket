import { HttpClient, HttpInterceptorFn } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

export type Role = 'USER' | 'SUPPORT' | 'ADMIN';
export type Priority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export type TicketStatus = 'OPEN' | 'IN_PROGRESS' | 'RESOLVED';

export interface Ticket { id: number; title: string; description: string; priority: Priority; status: TicketStatus; createdBy: string; createdAt: string; updatedAt: string; }
export interface Credentials { username: string; password: string; }
export interface AuthResponse { accessToken: string; tokenType: string; username: string; role: Role; }
export interface TicketPayload { title: string; description: string; priority: Priority; status?: TicketStatus; }

const API_URL = 'http://localhost:8080/api';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private http = inject(HttpClient);
  login(body: Credentials): Observable<AuthResponse> { return this.http.post<AuthResponse>(`${API_URL}/auth/login`, body); }
  register(body: Credentials): Observable<AuthResponse> { return this.http.post<AuthResponse>(`${API_URL}/auth/register`, body); }
  tickets(): Observable<Ticket[]> { return this.http.get<Ticket[]>(`${API_URL}/tickets`); }
  myTickets(): Observable<Ticket[]> { return this.http.get<Ticket[]>(`${API_URL}/tickets/mytickets`); }
  ticketById(id: number): Observable<Ticket> { return this.http.get<Ticket>(`${API_URL}/tickets/${id}`); }
  createTicket(body: TicketPayload): Observable<Ticket> { return this.http.post<Ticket>(`${API_URL}/tickets`, body); }
  updateTicket(id: number, body: Required<TicketPayload>): Observable<Ticket> { return this.http.put<Ticket>(`${API_URL}/tickets/${id}`, body); }
  deleteTicket(id: number): Observable<void> { return this.http.delete<void>(`${API_URL}/tickets/${id}`); }
}

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const token = localStorage.getItem('pulse-token');
  return next(token ? request.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : request);
};
