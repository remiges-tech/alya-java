import { TestBed } from '@angular/core/testing';

import { NgxAlyavalidationService } from './ngx-alyavalidation.service';

describe('NgxAlyavalidationService', () => {
  let service: NgxAlyavalidationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NgxAlyavalidationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
