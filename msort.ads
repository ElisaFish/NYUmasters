package Msort is
    LENGTH : integer := 40;
    type my_int is range -300 .. 300;
    type array_range is array (1 .. LENGTH) of my_int;
    procedure Sort (A: in out array_range);
end Msort;