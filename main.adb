with Msort;
use Msort;
with Text_IO;
use Text_IO;
with Ada_Integer;
use Ada_Integer;

procedure Main is
    type Arr_Type is array (Integer range -300..300) of Integer;
    A: Arr_Type (1..LENGTH);
    entry Finished_Reading(A: Arr_Type );

task Reader;

task body Reader is
    Input : File_Type;
    --x : Integer;
begin
    --Open(Input, In_File, "input.txt");
    for i in 1..LENGTH loop
        get(A(i));
    end loop;
    --Close(Input);
    Finished_Reading(A);
end Reader;

task Sum is
    entry Sorted;
end Sum;

task body Sum is
    sum_array : Integer;
begin
    accept Sorted do
       for i in 1..LENGTH loop
          sum_array := sum_array + A(i);
       end loop;
    end Sorted;
    Printer.Print(sum_array);
end Sum;

task Printer is
    entry Sorted;
    entry Print(sum_array: Int_array);
end Printer;

task body Printer is
begin
    accept Sorted do
       for i in 1..LENGTH loop
          put(A(i));
       end loop;
    end Sorted;
    
    accept Print(sum_array) do
        put(sum_array);
    end Print;
end Printer;

begin
    accept Finished_Reading(A) do
        Msort;
    end Finished_Reading;
    Printer.Sorted(A);
    Sum.Sorted(A);
    
end Main;