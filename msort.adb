with Ada.Integer_Text_IO; use Ada.Integer_Text_IO;
with Text_IO; use Text_IO;
with Msort; use Msort;

package body Msort is

    procedure Sort (A: in out array_range) is

        procedure Mergesort(Low:Integer; High:Integer) is
            mid : Integer := (Low + High) / 2;
            mid1 : Integer := (if mid < High then mid + 1 else High);
            task sort_left; -- Low .. mid
            task sort_right; -- mid1 .. High
            task merge is
                entry Left_Ready;
                entry Right_Ready;
            end merge;

            task body sort_left is -- Low .. mid
            begin
                if Low < mid then
                    Mergesort(Low, mid);
                end if;
                merge.Left_Ready;
            end sort_left;
            
            task body sort_right is -- mid1 .. High
            begin
                if mid1 < High then
                    Mergesort(mid1, High);
                end if;
                merge.Right_Ready;
            end sort_right;

            task body merge is
                left: Boolean := FALSE;
                right: Boolean := FALSE;
                i: Integer := Low;
                j: Integer := mid1;
                temp: my_int := A(i);

            begin -- task merge
                loop
                    select
                        accept Left_Ready do
                            left := TRUE;
                        end Left_Ready;
                    or
                        accept Right_Ready do
                            right := TRUE;
                        end Right_Ready;
                    end select;
                exit when left and right;
                end loop;

                for i in Low..High loop
                    if A(i) > A(j) then
                        A(Low .. High) := A(Low .. (i-1)) & A(j) & A(i .. (j-1)) & A((j+1) .. High);
                        if j < High then
                            j := j + 1;
                        end if;
                    end if;
                end loop;
            end merge;

        begin --Mergesort
            null;
        end Mergesort;

    begin -- Sort
        Mergesort(1, LENGTH);
    end Sort;
end Msort;