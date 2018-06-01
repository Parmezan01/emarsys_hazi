The calculateDueDate method

As an entry the method is accepting two parameters:
    - dateAndTime of class DateAndTime
    - turnaroundTime of int type

The DateAndTimeClass has a constructor witch accepts five integers denoting a date and time.
The order for initialization is (YYYY, MM, DD, hh, mm). The object is then converted to a
LocalDateTime object with the necessary checking if a valid date was entered.

At this time the turnaroundTime must be a positive integer. This can be easily changed with
a roundup function, or with implementing of a minute converter in order to accept floating
numbers.

A separate test class was provided with unit tests.