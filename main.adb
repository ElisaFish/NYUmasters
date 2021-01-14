with Msort; use Msort;
with Text_IO; use Text_IO;
with Ada.Integer_Text_IO; use Ada.Integer_Text_IO;

procedure Main is
    A: array_range; -- (1..LENGTH) of my_int in range -300 .. 300;

    task Reader is
        entry Done;
    end Reader;

    task Sum is
        entry Sorted;
    end Sum;

    task Printer is
        entry Sorted;
        entry Print(sum_array: Integer);
    end Printer;

    task body Reader is
        int : Integer;
    begin
        for i in 1..LENGTH loop
            get(int);
            A(i) := my_int(int);
        end loop;
        accept Done;
    end Reader;

    task body Sum is
        sum_array : Integer := 0;
    begin
        accept Sorted;
        for i in 1..LENGTH loop
            sum_array := sum_array + Integer(A(i));
        end loop;
        Printer.Print(sum_array);
    end Sum;

    task body Printer is
    begin
        accept Sorted;
        for i in 1..LENGTH loop
            put(Integer(A(i)));
        end loop;
        new_line;
        
        accept Print(sum_array: Integer) do
            put(sum_array);
        end Print;
    end Printer;

begin --Main
    Reader.Done;
    Msort.Sort(A);
    Printer.Sorted;
    Sum.Sorted;
end Main;

--[-297, -271, -217, -198, -153, -145, -123, -122, -118, -108, -106, -102, -82, -44, -34, -30, -21, -11, 18, 24, 40, 58, 68, 81, 97, 101, 110, 127, 137, 141, 154, 154, 178, 232, 234, 234, 255, 279, 286, 291]
--1117