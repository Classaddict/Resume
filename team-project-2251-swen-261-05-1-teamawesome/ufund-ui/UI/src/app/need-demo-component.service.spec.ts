import { TestBed } from '@angular/core/testing';

import { NeedDemoComponentService } from './need-demo-component.service';

describe('NeedDemoComponentService', () => {
  let service: NeedDemoComponentService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NeedDemoComponentService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
