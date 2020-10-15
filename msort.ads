package Msort
    integer : LENGTH := 40;
    type array_range is array of Integer range -300..300;
    A: array_range (1..LENGTH);
    procedure Sort (array_range: in out A);
end Msort;