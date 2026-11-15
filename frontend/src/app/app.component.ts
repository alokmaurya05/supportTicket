import { CommonModule } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { ApiService, AuthResponse, Priority, Role, Ticket, TicketPayload, TicketStatus } from './api.service';

@Component({
  selector: 'app-root', standalone: true, imports: [CommonModule, ReactiveFormsModule], templateUrl: './app.component.html', styleUrl: './app.component.css'
})
export class AppComponent {
  private api = inject(ApiService); private fb = inject(FormBuilder);
  readonly priorities: Priority[] = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];
  readonly statuses: TicketStatus[] = ['OPEN', 'IN_PROGRESS', 'RESOLVED'];
  user = signal<AuthResponse | null>(this.restoreUser()); tickets = signal<Ticket[]>([]); selected = signal<Ticket | null>(null);
  showAuth = signal(!this.user()); authMode = signal<'login' | 'register'>('login'); showCreate = signal(false); loading = signal(false); notice = signal<string | null>(null); filter = signal<'ALL' | TicketStatus>('ALL');
  filteredTickets = computed(() => this.filter() === 'ALL' ? this.tickets() : this.tickets().filter(t => t.status === this.filter()));
  authForm = this.fb.nonNullable.group({ username: ['', [Validators.required, Validators.minLength(3)]], password: ['', [Validators.required, Validators.minLength(8)]] });
  createForm = this.fb.nonNullable.group({ title: ['', [Validators.required, Validators.maxLength(150)]], description: ['', [Validators.required, Validators.maxLength(4000)]], priority: ['MEDIUM' as Priority, Validators.required] });
  editForm = this.fb.nonNullable.group({ title: ['', Validators.required], description: ['', Validators.required], priority: ['MEDIUM' as Priority, Validators.required], status: ['OPEN' as TicketStatus, Validators.required] });
  ticketLookupForm = this.fb.nonNullable.group({ id: [null as number | null, [Validators.required, Validators.min(1), Validators.pattern('^[0-9]+$')]] });
  constructor() { if (this.user()) this.loadTickets(); }
  get isStaff() { return this.user()?.role === 'SUPPORT' || this.user()?.role === 'ADMIN'; }
  get isAdmin() { return this.user()?.role === 'ADMIN'; }
  statusCount(status: TicketStatus) { return this.tickets().filter(t => t.status === status).length; }
  submitAuth() {
    if (this.authForm.invalid) return this.authForm.markAllAsTouched(); this.loading.set(true); this.notice.set(null);
    const request = this.authMode() === 'login' ? this.api.login(this.authForm.getRawValue()) : this.api.register(this.authForm.getRawValue());
    request.subscribe({ next: user => { this.saveUser(user); this.user.set(user); this.filter.set('ALL'); this.showAuth.set(false); this.loading.set(false); this.notice.set(`Welcome back, ${user.username}.`); this.loadTickets(); }, error: err => this.handleError(err) });
  }
  logout() { localStorage.removeItem('pulse-token'); localStorage.removeItem('pulse-user'); this.user.set(null); this.tickets.set([]); this.selected.set(null); this.showAuth.set(true); }
  loadTickets() {
    const request = this.user()?.role === 'USER' ? this.api.myTickets() : this.api.tickets();
    this.loading.set(true);
    request.subscribe({ next: data => { this.tickets.set(data); this.loading.set(false); }, error: err => this.handleError(err) });
  }
  lookupTicket() {
    if (this.ticketLookupForm.invalid) return this.ticketLookupForm.markAllAsTouched();
    const id = this.ticketLookupForm.controls.id.value;
    if (id === null) return;
    this.loading.set(true); this.notice.set(null);
    this.api.ticketById(id).subscribe({ next: ticket => { this.select(ticket); this.loading.set(false); }, error: err => this.handleError(err) });
  }
  createTicket() {
    if (this.createForm.invalid) return this.createForm.markAllAsTouched(); this.loading.set(true);
    this.api.createTicket(this.createForm.getRawValue()).subscribe({ next: ticket => { this.createForm.reset({ title: '', description: '', priority: 'MEDIUM' }); this.showCreate.set(false); this.loading.set(false); this.notice.set(`Ticket #${ticket.id} created successfully.`); this.loadTickets(); }, error: err => this.handleError(err) });
  }
  select(ticket: Ticket) { this.selected.set(ticket); this.editForm.setValue({ title: ticket.title, description: ticket.description, priority: ticket.priority, status: ticket.status }); }
  saveTicket() {
    const ticket = this.selected(); if (!ticket || this.editForm.invalid) return this.editForm.markAllAsTouched(); this.loading.set(true);
    this.api.updateTicket(ticket.id, this.editForm.getRawValue()).subscribe({ next: saved => { this.tickets.update(all => all.map(t => t.id === saved.id ? saved : t)); this.select(saved); this.loading.set(false); this.notice.set(`Ticket #${saved.id} updated.`); }, error: err => this.handleError(err) });
  }
  deleteTicket() { const ticket = this.selected(); if (!ticket || !confirm(`Delete ticket #${ticket.id}? This cannot be undone.`)) return; this.loading.set(true); this.api.deleteTicket(ticket.id).subscribe({ next: () => { this.tickets.update(all => all.filter(t => t.id !== ticket.id)); this.selected.set(null); this.loading.set(false); this.notice.set(`Ticket #${ticket.id} deleted.`); }, error: err => this.handleError(err) }); }
  closePanels() { this.showCreate.set(false); this.selected.set(null); }
  private saveUser(user: AuthResponse) { localStorage.setItem('pulse-token', user.accessToken); localStorage.setItem('pulse-user', JSON.stringify(user)); }
  private restoreUser(): AuthResponse | null { try { return JSON.parse(localStorage.getItem('pulse-user') || 'null') as AuthResponse | null; } catch { return null; } }
  private handleError(error: HttpErrorResponse) { this.loading.set(false); this.notice.set(error.error?.message || (error.status === 0 ? 'Cannot reach the API. Start the Spring Boot server on port 8080.' : 'Something went wrong. Please try again.')); }
}
