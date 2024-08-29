import { Injectable } from '@angular/core';
import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { PhoneNumberUtil } from 'google-libphonenumber';

@Injectable({
  providedIn: 'root'
})
export class RemigesAlyaValidatorService {

  constructor() { }

  panRegex = /^[A-Z]{5}[0-9]{4}[A-Z]{1}$/; //ex:ASDFG1234K
  emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/; //EX:test123@gmail.com 
  aadharRegex = /^(\d{4}[ -]?\d{4}[ -]?\d{4})$/; // ex:1234 5678 9123 |  123456789123 | 1234-5678-9123 
  gstRegex = /^[0-9]{2}[A-Za-z]{5}\d{4}[A-Za-z]{1}\d{1}[A-Za-z0-9]{2}$/; //ex:22AAAAA0000A1Z5
  drivingLicenseRegex = /^(([A-Z]{2}[0-9]{2})( )|([A-Z]{2}-[0-9]{2}))((19|20)[0-9][0-9])[0-9]{7}$/; //ex:HR-0619850034761 OR HR06 19850034761
  poNameRegex = /^[a-zA-Z\s.]+$/; //
  phonenoRegex = /^\d{10}$/; // Ex:7020123123
  patternphonenoRegex = /^\(?([0-9]{3})\)?[-. ]?([0-9]{3})[-. ]?([0-9]{4})$/; //222-055-9034, 321.789.4512 or 123 256 4587 formats.
  contrycodephonenoRegex = /^\(?([0-9]{3})\)?[-. ]?([0-9]{3})[-. ]?([0-9]{4})$/; // Ex.: either in +24-0455-9034, +21.3789.4512 or +23 1256 4587 format.
  contrycodephonenoRegex1 = /^[[+][0-9]{1,3}[0-9]{10}]*$/; //mobileno with country code +91 +1 ex. Ex: +19878767657 , +919878787678, +3551231223234
  passwordRegex = /^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*])(?=.{8,})/; // Password must contain more than 8 characters, 1 upper case letter, and 1 special character ex:Test@1234
  urlRegex = /^(https?:\/\/)[-a-zA-Z0-9@:%._+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_+.~#?&//=]*)$/; //https://www.google.com/  | http://www.google.com/ 
  cardRegex = /^(\d{4}[ -]?\d{4}[ -]?\d{4}[ -]?\d{4})$/; //16 digit card number ex:1234 5678 9123  1234|  1234567891231234 | 1234-5678-9123-1234 
  visacardRegex = /^(?:4[0-9]{12}(?:[0-9]{3})?)$/; //validate a Visa card starting with 4, length 13 or 16 digits. ex: 4123121212121212 |4123121212123
  mastercardRegex = /^(?:5[1-5][0-9]{14})$/; // validate a MasterCard starting with 51 through 55, length 16 digits.ex: 5112121212121212
  americanexpresscardRegex = /^(?:3[47][0-9]{13})$/; //validate a American Express credit card starting with 34 or 37, length 15 digits.ex: 341231234343212 | 373434345623456 
  discovercardRegex = /^(?:6(?:011|5[0-9][0-9])[0-9]{12})$/; //validate a Discover Card starting with 6011, length 16 digits  ex: 6011123123123123 
  jcbcardRegex = /^(?:(?:2131|1800|35\d{3})\d{11})$/; // validate a JCB card starting with 2131 or 1800, length 15 digits or starting with 35, length 16 digits. ex:3530 111333300000 | 3530111333300000
  dinnerclubcardRegex = /^(?:3(?:0[0-5]|[68][0-9])[0-9]{11})$/; //validate a Diners Club card starting with 300 through 305, 36, or 38, length 14 digits.ex:38520000023237 | 30569309025904
  alphwithspaceRegex = /^[a-zA-Z\s]*$/; // Regular expression to allow only alphabets and spaces Ex:test testcode
  onlynumberRegex = /^[0-9]*$/; // Regular expression to allow only numbers ex:1234567555
  numberwithspaceRegex = /^[0-9\s]*$/; // Regular expression to allow only numbers ex:1234567555 1232
  onlyalphanumericRegex = /^[a-zA-Z0-9_]*$/; // Regular expression to allow only numbers  ex:asdf123321
  alphaspecialCharRegex = /^[a-zA-Z\s&.-]*$/; //Validation to check if value includes only alphabets spaces and special characters(& . -)
  vinnoRegex = /(?=.*\d|=.*[A-Z])(?=.*[A-Z])[A-Z0-9]{17}/; ///validation For VIN No Ex:1HGBH41JXMN109186 
  dateRegex = /^\d{4}-\d{2}-\d{2}$/; // date Formate Ex.: YYYY-MM-DD
  timeRegex = /^([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$/; // Regex for hh:mm:ss format
  ISBNRegex = /^(?:ISBN(?:-1[03])?:? )?(?=(?:[-0-9 ]{17}|[-0-9X ]{13}|[0-9X]{10})$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?(?:[0-9]+[- ]?){2}[0-9X]$/; // A 13-digit ISBN, 978-3-16-148410-0, as represented by an EAN-13 bar code
  IBANRegex = /^[A-Z]{2}\d{2}[A-Z0-9]{11,}$/; // A typical IBAN is made up of a string of symbols that each stand for a step in the transfer of funds. Every country uses a similar format, however different locations use different numbers of digits.
  //Some nations employ 20 characters, whereas others employ only 15 characters. The most characters that a nation can utilise for the IBAN is about 34.
 coordinatesRegex = /^-?([1-8]?[1-9]|[1-9]0)\.\d{1,6},\s*-?(1?[0-7]?[0-9]|180|[0-9]{1,2})\.\d{1,6}$/; //Decimal degrees (DD): 41.40338, 2.17403.
  currencyRegex = /^-?\d+(\.\d{1,2})?$/; //inamount 12.1111
  passportnoRegex = /^[A-Za-z0-9]{6,10}$/; //A123222  min 6 & max 10 characters
  colorhexRegex = /^#[0-9A-F]{6}$/i; //#FFFFFF (white)#000000 (black)#FF0000 (red) #00FF00 (green)#0000FF (blue) #FFFF00 (yellow) max 7 Characters

  // Define a map to store error messages
  panRegexMsg = "Enter a valid PAN Card number (e.g., ASDFG1234K)";
  emailRegexMsg = "Enter a valid email address";
  aadharRegexMsg = "Enter a valid Aadhar Card number (e.g., 1234 5678 9123)";
  gstRegexMsg = "Enter a valid GST number (e.g., 22AAAAA0000A1Z5)";
  drivingLicenseRegexMsg = "Enter a valid Driving License number. (e.g., HR-0619850034761 OR HR06 19850034761)";
  poNameRegexMsg = "Enter a valid name (alphabets, spaces, periods)";
  phonenoRegexMsg = "Enter a valid 10-digit phone number";
  patternphonenoRegexMsg = "Enter phone number in format 222-055-9034, 321.789.4512, or 123 256 4587";
  contrycodephonenoRegexMsg = "Enter phone number with country code in format +24-0455-9034, +21.3789.4512, or +23 1256 4587";
  contrycodephonenoRegex1Msg = "Enter phone number with country code";
  passwordRegexMsg = "Password must have at least 8 characters with 1 uppercase, 1 lowercase, 1 number, and 1 special character";
  urlRegexMsg = "Enter a valid URL";
  cardRegexMsg = "Enter a valid 16-digit card number";
  visacardRegexMsg = "Enter a valid Visa card number starting with 4";
  mastercardRegexMsg = "Enter a valid MasterCard number starting with 51 through 55";
  americanexpresscardRegexMsg = "Enter a valid American Express card number starting with 34 or 37";
  discovercardRegexMsg = "Enter a valid Discover card number starting with 6011";
  jcbcardRegexMsg = "Enter a valid JCB card number starting with 2131 or 1800, or starting with 35";
  dinnerclubcardRegexMsg = "Enter a valid Diners Club card number starting with 300 through 305, 36, or 38";
  alphawithspaceRegexMsg = "Enter alphabets and spaces only";
  onlynumberRegexMsg = "Enter numbers only";
  numberwithspaceRegexMsg = "Enter numbers and spaces only";
  onlyalphanumericRegexMsg = "Enter alphanumeric characters only";
  alphaspecialCharRegexMsg = "Enter alphabets, spaces, and special characters only";
  vinnoRegexMsg = "Enter a valid VIN number (e.g., 1HGBH41JXMN109186 )";
  dateRegexMsg = 'Invalid date format. Please use YYYY-MM-DD.';
  timeRegexMsg = 'Invalid time format. Please use hh:mm:ss';
  ISBNRegexMsg = "Enter a valid ISBN number (e.g. for ISBN-13, 978-3-16-148410-0, & ISBN-10 0-596-52068-9)";
  IBANRegexMsg = "Enter a valid IBAN number (e.g., FR1420041010050500013M02606 )";
  coordinatesRegexMsg = "Enter Valid Coordinates in DD (e.g. 41.40338, 2.17403)"
  currencyRegexMsg = "Invalid currency format. Please enter a valid currency amount.(e,g. 12.22)"
  passportnoRegexMsg = "Invalid passport number format. Please enter a valid passport number.(e.g. A2096457)"
  colorhexRegexMsg = "Invalid color code format. Please enter a valid color code.(e.g. #FFFFFF)"

  // function for to pass the pattern and error massege 
  customRegexValidator(pattern: RegExp, errorMsg: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value as string;

      if (pattern.test(value)) {
        return null; // Validation passed; the input matches the custom regex pattern.
      } else {
        return { 'customRegex': `'${value}',  ${errorMsg}` }; // Validation failed; the input does not match the pattern.
      }
    };
  }


  // validation to check two input filed are match ex:password and confirm password
  isMatchValidator(controlName: string, matchingControlName: string) {
    return (formGroup: FormGroup) => {
      let control = formGroup.controls[controlName];
      let matchingControl = formGroup.controls[matchingControlName]
      if (
        matchingControl.errors &&
        !matchingControl.errors['matchValidator']
      ) {
        return;
      }
      if (control.value !== matchingControl.value) {
        matchingControl.setErrors({ ['matchValidator']: true });
      } else {
        matchingControl.setErrors(null);
      }
    };
  }



  // validattion to check IP Address Ex. IPV4:"123.1.1.221" ,Ex. IPV6:"2001:0db8:85a3:0000:0000:8a2e:0370:7334"
  ipAddressValidator(control: AbstractControl): { [key: string]: any } | null {
    const ip = control.value;
    const ipv4Regex = /^(\d{1,3}\.){3}\d{1,3}$/;
    const ipv6Regex = /^([\da-f]{1,4}:){7}[\da-f]{1,4}$/i;

    if (ipv4Regex.test(ip)) {
      const isValid = ip.split('.').every((part: string) => parseInt(part, 10) <= 255);
      return isValid ? null : { 'invalidIpv4': true };
    }

    if (ipv6Regex.test(ip)) {
      const isValid = ip.split(':').every((part: string | any[]) => part.length <= 4);
      return isValid ? null : { 'invalidIpv6': true };
    }

    return { 'invalidIp': true };
  }


  // validattion to check File Type and File Size "
  validateAndUploadFile(file: File, allowedTypes: string[], maxSizeInBytes: number): Promise<string | null> {
    return new Promise((resolve, reject) => {
      // Validate file type
      if (!file.type || !allowedTypes.includes(file.type)) {
        reject(new Error('Invalid file type'));
        return; // Return to prevent further execution
      }

      // Validate file size (if needed)
      if (file.size > maxSizeInBytes) {
        reject(new Error('File size exceeds limit'));
        return; // Return to prevent further execution
      }
       // Simulating file upload with a timeout
      setTimeout(() => {
        resolve('File uploaded successfully');
      }, 1000); // Simulating 1 seconds of upload time
    });
  }


 // Custom validation function for to show the  minimum length Massage
  minLengthValidationMessage(minLength: number) {
    return `Should have at least ${minLength} characters`;
  }
  // Custom validation function for to show the  maximum length Massage

  maxLengthValidationMessage(maxLength: number) {
    return `This value should be less than ${maxLength} characters`;
  }


  // Custom validation function for minimum length
  validateMinLength(control: AbstractControl): ValidationErrors | null {
    const minLength = control?.errors?.['minlength']?.requiredLength;
    const value = control?.value;

    if (minLength && value) {
      return value.length < minLength ? { minlength: true } : null;
    }

    return null;
  }

  // Custom validation function for maximum length
  validateMaxLength(control: AbstractControl): ValidationErrors | null {
    const maxLength = control?.errors?.['maxlength']?.requiredLength;
    const value = control?.value;
    if (maxLength && value) {
      return value.length > maxLength ? { maxlength: true } : null;
    }
    return null;
  }

  // validation of min value and max value 
  rangeValidator(min: number, max: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value as number;

      if (isNaN(value) || value < min || value > max) {
        return { 'range': `Input must be between ${min} and ${max}` };
      }

      return null; // Validation passed; the input falls within the specified range.
    };
  }

  // validation for  date range 
  dateRangeValidator(control: any) {
    const value = new Date(control.value);
    const currentDate = new Date();
    const minDate = new Date(currentDate.getFullYear() - 100, currentDate.getMonth(), currentDate.getDate());
    const maxDate = new Date(currentDate.getFullYear() + 100, currentDate.getMonth(), currentDate.getDate());

    if (value < minDate || value > maxDate) {
      return { invalidDateRange: true };
    }
   return null;
  }

 validDateValidator(control: any) {
    const value = control.value;
    const isValidDate = !isNaN(Date.parse(value));

    if (!isValidDate) {
      return { invalidDate: true };
    }
 return null;
  }

  //validation for if age is less than 18 year
  ageValidator(control: AbstractControl) {
    const value = new Date(control.value);
    const today = new Date();
    const minAgeDate = new Date(today.getFullYear() - 18, today.getMonth(), today.getDate());

    if (value >= minAgeDate) {
      return { underAge: true };
    }
    return null;
  }

//keyup or keypress Event
  allowOnly(event: KeyboardEvent, pattern: RegExp) {
    const inputChar = String.fromCharCode(event.charCode);

    if (!pattern.test(inputChar)) {
      // If the input character does not match the pattern, prevent the default action
      event.preventDefault();
    }
  }

  //phone number validator  using google-libphonenumber validator
  phoneNumberUtil = PhoneNumberUtil.getInstance();
 isPhoneNumberValidator(regionCode: any = undefined): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } => {
      let validNumber = false;
      try {
        const phoneNumber = this.phoneNumberUtil.parseAndKeepRawInput(
          control.value, regionCode
        );
        validNumber = this.phoneNumberUtil.isValidNumber(phoneNumber);
      } catch (e) { }

      return validNumber ? {} : { 'wrongNumber': { value: control.value } };
    }
  }

   // General function to convert form input to uppercase and trim length
   handleInputToUppercase(form: FormGroup, controlName: string, maxLength: number = 10): void {
    const currentValue = form.controls[controlName].value;
    if (currentValue) {
      const uppercaseValue = currentValue.toUpperCase().substring(0, maxLength);
      form.controls[controlName].setValue(uppercaseValue);
    }
  }
}
