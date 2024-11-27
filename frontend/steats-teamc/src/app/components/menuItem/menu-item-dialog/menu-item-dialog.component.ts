import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {FormControl, Validators} from '@angular/forms';
import {MenuItem} from '../../../models/menuItem.model';

@Component({
  selector: 'app-menu-item-dialog',
  templateUrl: './menu-item-dialog.component.html',
  styleUrls: ['./menu-item-dialog.component.scss']
})
export class MenuItemDialogComponent {
  quantity = new FormControl(1, [Validators.required, Validators.min(1)]);

  constructor(
    public dialogRef: MatDialogRef<MenuItemDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: MenuItem
  ) {
  }

  onAddToCart(): void {
    if (this.quantity.valid) {
      this.dialogRef.close({quantity: this.quantity.value});
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
