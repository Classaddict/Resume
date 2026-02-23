import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Need {
  cost: number;
  quantity: number;
  type: string;
}  

@Injectable({
  providedIn: 'root'
})
export class NeedService {
  private apiUrl = 'http://localhost:8080';
  constructor(private http: HttpClient) {}

  ListNeed(): Observable<Need[]> {
    return this.http.get<Need[]>(this.apiUrl);
  }

  DeleteNeed(type: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${type}`);
  }

  searchNeed(type: string): Observable<Need[]> {
    return this.http.get<Need[]>(`${this.apiUrl}/${type}`);
  }



}
