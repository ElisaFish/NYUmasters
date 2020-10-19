% For an even number of columns, my answer differs from the professor's,
% but the answer still works mathematically.

function [FOUND, P] = InvertMidpoints(Q)
    %Qx,Qy = Q
    Qx = Q(1,:);
    Qy = Q(2,:);
    k = length(Qx);
    is_even = mod(k,2) == 0;
    FOUND = 1;
    
    C = eye(k);
    for i = 1:k-1
        C(i,i+1) = 1;
    end
    C(k,1) = 1;
    C = 1/2 .* C;
    
    if is_even
        last_Eqn = C(k,:);
        last_Qx = Qx(k);
        last_Qy = Qy(k);
        C = C(1:k-1,:);
        Qx = Qx(1:k-1);
        Qy = Qy(1:k-1);
    end
        
    %Px = inv(C) * Qx'
    %Py = inv(C) * Qy'
    Px = C \ Qx';
    Py = C \ Qy';
    P = [Px';Py']
    
    % Check if solution satisfies last equation
    if is_even
        C_new = C;
        C_new(k,:) = last_Eqn;
        Qx(k) = last_Qx
        Qy(k) = last_Qy
        Qx_new = C_new * Px
        Qy_new = C_new * Py
        for i=1:k
            if (Qx(i) - Qx_new(i) > 10^(-8)) || (Qy(i) - Qy_new(i) > 10^(-8))
                FOUND = 0
                P = zeros(2,k)
                break
            end
        end
    end
    
    for i = 1:k
        if Px(i) == NaN || Py(i) == NaN
            FOUND = 0
            P = zeros(2,k)
            break
        end
    end
    %return [FOUND, P]
end