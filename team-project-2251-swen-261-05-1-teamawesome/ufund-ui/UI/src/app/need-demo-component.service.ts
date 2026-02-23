import { Injectable, OnInit } from '@angular/core';
import { NeedService, Need } from './services/need.service';

@Injectable({
  providedIn: 'root'
})
export class NeedDemoComponent implements OnInit {
  needs: Need[] = [];

  constructor(private needService: NeedService) {}

  ngOnInit(): void {
    this.needService.ListNeed().subscribe(data => {
      this.needs = data;
    });
  }
}